package com.lightbend.paradox.markdown.directives.standard

import com.lightbend.paradox.markdown.directives.ContainerBlockDirective
import org.pegdown.Printer
import org.pegdown.ast.{DirectiveNode, Visitor}

/**
  * Wrap directive.
  *
  * Wraps inner content in a `div` or `p`, optionally with custom `id` and/or `class` attributes.
  */
case class WrapDirective(typ: String)
    extends ContainerBlockDirective(Array(typ, typ.toUpperCase): _*) {
  def render(node: DirectiveNode, visitor: Visitor, printer: Printer): Unit = {
    val id =
      node.attributes.identifier match {
        case null => ""
        case x    => s""" id="$x""""
      }
    val classes =
      node.attributes.classesString match {
        case "" => ""
        case x  => s""" class="$x""""
      }
    printer.print(s"""<$typ$id$classes>""")
    node.contentsNode.accept(visitor)
    printer.print(s"</$typ>")
  }
}
