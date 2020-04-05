package com.lightbend.paradox.markdown.directives.standard

import java.io.FileNotFoundException

import com.lightbend.paradox.markdown.directives.{InlineDirective, SourceDirective}
import com.lightbend.paradox.markdown.{Page, Snippet, Url}
import org.pegdown.Printer
import org.pegdown.ast.{DirectiveNode, ExpLinkNode, Visitor}

/**
  * Link to external sites using URI templates.
  */
abstract class ExternalLinkDirective(names: String*)
    extends InlineDirective(names: _*)
    with SourceDirective {

  import ExternalLinkDirective._

  def resolveLink(node: DirectiveNode, location: String): Url

  def render(node: DirectiveNode, visitor: Visitor, printer: Printer): Unit =
    new ExpLinkNode("", resolvedSource(node, page), node.contentsNode).accept(visitor)

  override protected def resolvedSource(node: DirectiveNode, page: Page): String = {
    val link = super.resolvedSource(node, page)
    try {
      val resolvedLink = resolveLink(node: DirectiveNode, link).base.normalize.toString
      if (resolvedLink startsWith (".../")) page.base + resolvedLink.drop(4) else resolvedLink
    } catch {
      case Url.Error(reason) =>
        throw new LinkException(
          s"Failed to resolve [$link] referenced from [${page.path}] because $reason"
        )
      case e: FileNotFoundException =>
        throw new LinkException(
          s"Failed to resolve [$link] referenced from [${page.path}] to a file: ${e.getMessage}"
        )
      case e: Snippet.SnippetException =>
        throw new LinkException(
          s"Failed to resolve [$link] referenced from [${page.path}]: ${e.getMessage}"
        )
    }
  }
}

object ExternalLinkDirective {

  /**
    * Exception thrown for unknown or invalid links.
    */
  class LinkException(reason: String) extends RuntimeException(reason)

}
