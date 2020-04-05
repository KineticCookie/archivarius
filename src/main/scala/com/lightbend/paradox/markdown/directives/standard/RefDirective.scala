package com.lightbend.paradox.markdown.directives.standard

import com.lightbend.paradox.markdown.directives.{InlineDirective, SourceDirective}
import com.lightbend.paradox.markdown.{Page, Path}
import org.pegdown.Printer
import org.pegdown.ast.{DirectiveNode, ExpLinkNode, Visitor}

/**
  * Ref directive.
  *
  * Refs are for links to internal pages. The file extension is replaced when rendering.
  * Links are validated to ensure they point to a known page.
  */
case class RefDirective(
    page: Page,
    pathExists: String => Boolean,
    convertPath: String => String,
    variables: Map[String, String]
) extends InlineDirective("ref", "ref:")
    with SourceDirective {

  def render(node: DirectiveNode, visitor: Visitor, printer: Printer): Unit =
    new ExpLinkNode("", check(convertPath(resolvedSource(node, page))), node.contentsNode)
      .accept(visitor)

  private def check(path: String): String = {
    if (!pathExists(Path.resolve(page.path, path)))
      throw new RefDirective.LinkException(s"Unknown page [$path] referenced from [${page.path}]")
    path
  }
}

object RefDirective {

  /**
    * Exception thrown for unknown pages in reference links.
    */
  class LinkException(message: String) extends RuntimeException(message)

}
