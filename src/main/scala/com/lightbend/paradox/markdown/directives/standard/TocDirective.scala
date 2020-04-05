package com.lightbend.paradox.markdown.directives.standard

import com.lightbend.paradox.markdown.directives.LeafBlockDirective
import com.lightbend.paradox.markdown.{Page, TableOfContents}
import com.lightbend.paradox.tree.Tree.Location
import org.pegdown.Printer
import org.pegdown.ast.{DirectiveNode, Visitor}

/**
  * Table of contents directive.
  *
  * Placeholder to insert a serialized table of contents, using the page and header trees.
  * Depth and whether to include pages or headers can be specified in directive attributes.
  */
case class TocDirective(location: Location[Page], includeIndexes: List[Int])
    extends LeafBlockDirective("toc") {
  def render(node: DirectiveNode, visitor: Visitor, printer: Printer): Unit = {
    val classes = node.attributes.classesString
    val depth   = node.attributes.intValue("depth", 6)
    val pages   = node.attributes.booleanValue("pages", true)
    val headers = node.attributes.booleanValue("headers", true)
    val ordered = node.attributes.booleanValue("ordered", false)
    val toc     = new TableOfContents(pages, headers, ordered, depth)
    printer.println.print(s"""<div class="toc $classes">""")
    toc.markdown(location, node.getStartIndex, includeIndexes).accept(visitor)
    printer.println.print("</div>")
  }
}
