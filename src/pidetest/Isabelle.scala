/*  
 A very simple Isabelle plugin
*/

package pidetest

import scala.actors.Actor

import isabelle._

object Isabelle
{

  var session: Option[Session] = None

  def start(path: String, logic: String, thys: List[String]) {
    val thy_load = new Thy_Load()
    val thy_info = new Thy_Info(thy_load)

    println("Initialising Isabelle...")
    Isabelle_System.init(path)

    val s = new Session(thy_load)
    val smgr = new SessionActor(s, thys)
 
    println("Starting actors...")
    s.phase_changed += smgr 
    smgr.start()
    /* Start session */
    println("Starting sesssion...")
    s.start(Time.seconds(30), List(logic))
    println("All started.")
    }
 
}

class SessionActor(val s: Session, val thys: List[String]) extends Actor {
  def act () {
    loop { react {
      case Session.Ready => 
        Isabelle.session = Some(s); 
        println("Isabelle is ready.")
        val rm= new RawMsgActor("Raw message")
        s.all_messages += rm
        // The output from s.raw_messages is voluminous, but 
        // it seems not the be the case that s.raw_output_messages
        // contains exactly Result messages from s.raw_messages, so we have
        // to sift through it...
        rm.start()
        // s.syslog_messages += rm -- same holds for syslog
        for (thy<- thys) FileServer.read(thy)
      case Session.Failed => Isabelle.session = None
    }}
  }
}


class RawMsgActor(val pre : String) extends Actor {
  def act () {
    loop { react {
        case result: Isabelle_Process.Message =>
          println(pre+ ": "+ result) 
        // case input: Isabelle_Process.Input =>
        //   println("Raw input: "+ input)  // These are not that interesting
        // case bad => System.err.println("Raw output: ignoring bad message " + bad)
      }
    }
  }
}
