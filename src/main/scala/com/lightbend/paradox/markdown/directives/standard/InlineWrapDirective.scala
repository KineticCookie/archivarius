package com.lightbend.paradox.markdown.directives.standard

import com.lightbend.paradox.markdown.directives.InlineDirective
import org.pegdown.Printer
import org.pegdown.ast.{DirectiveNode, Visitor}

/**
  * Inline wrap directive
  *
  * Wraps inner contents in a `span`, optionally with custom `id` and/or `class` attributes.
  */
case class InlineWrapDirective(typ: String)
    extends InlineDirective(Array(typ, typ.toUpperCase): _*) {

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
