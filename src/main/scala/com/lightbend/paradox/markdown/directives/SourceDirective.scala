package com.lightbend.paradox.markdown.directives

import java.io.File

import com.lightbend.paradox.markdown.directives.standard.{RefDirective, SnipDirective}
import com.lightbend.paradox.markdown.{Page, PropertyUrl, Writer}
import org.pegdown.ToHtmlSerializer
import org.pegdown.ast.{DirectiveNode, ReferenceNode, RootNode}

import scala.jdk.CollectionConverters._

/**
  * Directives with defined "source" semantics.
  */
trait SourceDirective { this: Directive =>
  def page: Page
  def variables: Map[String, String]

  protected def resolvedSource(node: DirectiveNode, page: Page): String = {
    def ref(key: String) =
      referenceMap
        .get(key.filterNot(_.isWhitespace).toLowerCase)
        .map(_.getUrl)
        .getOrElse(
          throw new RefDirective.LinkException(s"Undefined reference key [$key] in [${page.path}]")
        )
    Writer.substituteVarsInString(
      node.source match {
        case x: DirectiveNode.Source.Direct => x.value
        case x: DirectiveNode.Source.Ref    => ref(x.value)
        case DirectiveNode.Source.Empty     => ref(node.label)
      },
      variables
    )
  }

  protected def resolveFile(
      propPrefix: String,
      source: String,
      page: Page,
      variables: Map[String, String]
  ): File =
    SourceDirective.resolveFile(propPrefix, source, page.file, variables)

  private lazy val referenceMap: Map[String, ReferenceNode] = {
    val tempRoot = new RootNode
    tempRoot.setReferences(page.markdown.getReferences)
    var result = Map.empty[String, ReferenceNode]
    new ToHtmlSerializer(None.orNull) {
      toHtml(tempRoot)
      result = references.asScala.toMap
    }
    result
  }
}

object SourceDirective {
  def resolveFile(
      propPrefix: String,
      source: String,
      pageFile: File,
      variables: Map[String, String]
  ): File =
    source match {
      case s if s startsWith "$" =>
        val baseKey = s.drop(1).takeWhile(_ != '$')
        val base    = new File(PropertyUrl(s"$propPrefix.$baseKey.base_dir", variables.get).base.trim)
        val effectiveBase =
          if (base.isAbsolute) base else new File(pageFile.getParentFile, base.toString)
        new File(effectiveBase, s.drop(baseKey.length + 2))
      case s if s startsWith "/" =>
        val base = new File(PropertyUrl(SnipDirective.buildBaseDir, variables.get).base.trim)
        new File(base, s)
      case s =>
        new File(pageFile.getParentFile, s)
    }
}
