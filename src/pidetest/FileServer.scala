package pidetest

import scala.actors.Actor
import scala.io.Source

import isabelle._

object FileServer {
  var thyDir = "/home/cxl/src/isabelle/pide-test/theories/"; /* Just the default value */ 
   
  def readFile(name : String) : String = {
    val src = Source.fromFile(thyDir + name)
    val lines = src.mkString
    src.close ()
    return lines
  }
 
  def read(name: String) {
      Isabelle.session match {
        case Some(s) => 
          val nn  = Document.Node.Name(Path.explode(name))
          val txt = readFile(name)
          val hdr = Exn.capture {Thy_Header.check(nn.theory, txt) }
          val rng = Text.Range(0, txt.length)
          val per = Text.Perspective(List(rng))
          val f   = new FileActor(s, nn, txt)
          s.commands_changed += f
          f.start()
          s.init_node(nn, hdr, per, txt)          
        case None =>
          println ("Isabelle not ready yet-- try again later.")
      }
  }
  
}

class FileActor(val s: Session, val name: Document.Node.Name, val txt: String) extends Actor {
    def act () {
        loop { react {
            case Session.Commands_Changed(nodes, cmds) =>
            // The nodes (i.e. theory files) and cmds are actually just upper bounds 
            // of the region of the document affected by the change. 
            // If you want to look at them, use this:
            //   println("Node: "+ nodes+ ", command tokens: "+ cmds.map (c=> c.span))
            //   println("Node: "+ nodes+ ", command source: "+ cmds.map (c=> c.source))
            // It turns out that e.g. the order of the cmds is irrelevant (unsurprisingly, given that they are a set).
            // This piece of code extracts the source and the markup of the affected nodes:
           for ( name<-nodes; snap= s.snapshot(name, List()); (cmd, off)<- snap.node.command_range() )
             println("Command offset: "+ off+ ", source: "+ cmd.source+ ", markup: "+ snap.command_state(cmd).markup)
        }}
    }
}
