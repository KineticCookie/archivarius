package com.lightbend.paradox.markdown.directives.standard

import com.lightbend.paradox.markdown.directives.InlineDirective
import org.pegdown.Printer
import org.pegdown.ast.{DirectiveNode, SpecialTextNode, Visitor}

/**
  * Var directive.
  *
  * Looks up property values and renders escaped text.
  */
case class VarDirective(variables: Map[String, String]) extends InlineDirective("var", "var:") {
  def render(node: DirectiveNode, visitor: Visitor, printer: Printer): Unit = {
    new SpecialTextNode(variables.get(node.label).getOrElse(s"<${node.label}>")).accept(visitor)
  }
}
