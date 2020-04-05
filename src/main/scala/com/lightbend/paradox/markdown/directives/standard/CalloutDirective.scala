package com.lightbend.paradox.markdown.directives.standard

import com.lightbend.paradox.markdown.directives.ContainerBlockDirective
import org.pegdown.Printer
import org.pegdown.ast.{DirectiveNode, Visitor}

/**
  * Callout directive.
  *
  * Renders call-out divs.
  */
case class CalloutDirective(name: String, defaultTitle: String)
    extends ContainerBlockDirective(Array(name): _*) {
  def render(node: DirectiveNode, visitor: Visitor, printer: Printer): Unit = {
    val classes = node.attributes.classesString
    val title   = node.attributes.value("title", defaultTitle)

    printer.print(s"""<div class="callout $name $classes">""")
    printer.print(s"""<div class="callout-title">$title</div>""")
    node.contentsNode.accept(visitor)
    printer.print("""</div>""")
  }
}
