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
    s.start(Time.seconds(30), false, List(logic))
    println("All started.")
    }
 
}

class SessionActor(val s: Session, val thys: List[String]) extends Actor {
  def act () {
    loop { react {
      case Session.Ready => 
        Isabelle.session = Some(s); 
        println("Isabelle is ready.")
        for (thy<- thys) FileServer.read(thy)
      case Session.Failed => Isabelle.session = None
    }}
  }
}

