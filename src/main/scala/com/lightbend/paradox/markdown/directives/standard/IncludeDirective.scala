package com.lightbend.paradox.markdown.directives.standard

import com.lightbend.paradox.markdown.Page
import com.lightbend.paradox.markdown.directives.{LeafBlockDirective, SourceDirective}
import org.pegdown.Printer
import org.pegdown.ast.{DirectiveNode, Visitor}

case class IncludeDirective(page: Page, variables: Map[String, String])
    extends LeafBlockDirective("include")
    with SourceDirective {

  override def render(node: DirectiveNode, visitor: Visitor, printer: Printer): Unit = {
    throw new IllegalStateException(
      "Include directive should have been handled in markdown preprocessing before render, but wasn't."
    )
  }
}

object IncludeDirective {

  case class IncludeSourceException(source: DirectiveNode.Source)
      extends RuntimeException(
        s"Only explicit links are supported by the include directive, reference links are not: " + source
      )

  case class IncludeFormatException(format: String)
      extends RuntimeException(s"Don't know how to include '*.$format' content.")

}
