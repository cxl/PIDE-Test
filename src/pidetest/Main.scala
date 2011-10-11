/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pidetest

import scala.io._
import java.io.File

object Main {

  /**
   * @param args the command line arguments
   */
  def main(args: Array[String]): Unit = {
    if (args.length < 1) { println("No argument!"); }
    FileServer.thyDir = args(0)
    val thys = for { file <- new File(args(0)).listFiles
                     name= file.getName 
                     if name.endsWith(".thy") } yield name
    Isabelle.start("/usr/local/Isabelle_24-Sep-2011", "HOL", thys.toList)
  }

}
