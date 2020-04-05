package com.lightbend.paradox.markdown.directives.standard

import com.lightbend.paradox.markdown.directives.InlineDirective
import org.pegdown.Printer
import org.pegdown.ast.{DirectiveNode, Visitor}

case class InlineGroupDirective(groups: Seq[String]) extends InlineDirective(groups: _*) {
  def render(node: DirectiveNode, visitor: Visitor, printer: Printer): Unit = {
    printer.print(s"""<span class="group-${node.name}">""")
    node.contentsNode.accept(visitor)
    printer.print(s"</span>")
  }
}
