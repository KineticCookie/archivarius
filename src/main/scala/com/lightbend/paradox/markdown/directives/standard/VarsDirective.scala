package com.lightbend.paradox.markdown.directives.standard

import com.lightbend.paradox.markdown.directives.ContainerBlockDirective
import org.pegdown.Printer
import org.pegdown.ast.{DirectiveNode, VerbatimNode, Visitor}

/**
  * Vars directive.
  *
  * Replaces property values in verbatim blocks.
  */
case class VarsDirective(variables: Map[String, String]) extends ContainerBlockDirective("vars") {
  def render(node: DirectiveNode, visitor: Visitor, printer: Printer): Unit = {
    import scala.collection.JavaConverters._
    node.contentsNode.getChildren.asScala.headOption match {
      case Some(verbatim: VerbatimNode) =>
        val startDelimiter = node.attributes.value("start-delimiter", "$")
        val stopDelimiter  = node.attributes.value("stop-delimiter", "$")
        val text = variables.foldLeft(verbatim.getText) {
          case (str, (key, value)) =>
            str.replace(startDelimiter + key + stopDelimiter, value)
        }
        new VerbatimNode(text, verbatim.getType).accept(visitor)
      case _ => node.contentsNode.accept(visitor)
    }
  }
}
