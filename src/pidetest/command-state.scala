
import isabelle._

object Command_State {
  type Content = (String, Markup_Tree)

  def stable_version_content(session: Session): Map[String, List[Content]] =
  {
    val state= session.current_state()
    val version= state.recent_stable.version.join

    for ((name, node) <- version.nodes) yield
       (name.node, node.commands.toList.map(cmd=> 
              (cmd.source, state.command_state(version, cmd).markup)))
  }
}

// use with command_state(Isabelle.session)

