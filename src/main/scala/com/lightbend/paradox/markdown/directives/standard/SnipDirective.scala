package com.lightbend.paradox.markdown.directives.standard

import java.io.FileNotFoundException
import java.util.Optional

import com.lightbend.paradox.markdown.directives.{LeafBlockDirective, SourceDirective}
import com.lightbend.paradox.markdown.{Page, Path, Snippet}
import org.pegdown.Printer
import org.pegdown.ast.{DirectiveNode, VerbatimGroupNode, Visitor}
import org.pegdown.ast.VerbatimGroupNode
import scala.jdk.CollectionConverters._

/**
  * Snip directive.
  *
  * Extracts snippets from source files into verbatim blocks.
  */
case class SnipDirective(page: Page, variables: Map[String, String])
    extends LeafBlockDirective("snip")
    with SourceDirective
    with GitHubResolver {

  def render(node: DirectiveNode, visitor: Visitor, printer: Printer): Unit = {
    try {
      val labels = node.attributes.values("identifier").asScala.toList
      val source = resolvedSource(node, page)
      val filterLabels = node.attributes
        .booleanValue("filterLabels", variables.get("snip.filterLabels").forall(_ == "true"))
      val file                = resolveFile("snip", source, page, variables)
      val (text, snippetLang) = Snippet(file, labels, filterLabels)
      val lang                = Option(node.attributes.value("type")).getOrElse(snippetLang)
      val group               = Option(node.attributes.value("group")).getOrElse("")
      val sourceUrl =
        if (variables.contains(GitHubResolver.baseUrl) && variables
              .getOrElse(SnipDirective.showGithubLinks, "false") == "true") {
          Optional.of(
            resolvePath(page, Path.toUnixStyleRootPath(file.getAbsolutePath), labels.headOption).base.normalize.toString
          )
        } else Optional.empty[String]()
      new VerbatimGroupNode(text, lang, group, node.attributes.classes, sourceUrl).accept(visitor)
    } catch {
      case e: FileNotFoundException =>
        throw new SnipDirective.LinkException(
          s"Unknown snippet [${e.getMessage}] referenced from [${page.path}]"
        )
    }
  }

}

object SnipDirective {

  val showGithubLinks = "snip.github_link"
  val buildBaseDir    = "snip.build.base_dir"

  /**
    * Exception thrown for unknown snip links.
    */
  class LinkException(message: String) extends RuntimeException(message)

}
