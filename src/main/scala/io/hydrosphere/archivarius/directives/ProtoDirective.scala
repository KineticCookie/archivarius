package io.hydrosphere.archivarius.directives

import com.lightbend.paradox.markdown.Page
import com.lightbend.paradox.markdown.directives.{
  InlineDirective,
  LeafBlockDirective,
  SourceDirective
}
import org.pegdown.Printer
import org.pegdown.ast.{DirectiveNode, ExpLinkNode, Visitor}

case class ProtoLinkDirective(
    page: Page,
    pathExists: String => Boolean,
    convertPath: String => String,
    variables: Map[String, String]
) extends LeafBlockDirective("proto") {
  def render(node: DirectiveNode, visitor: Visitor, printer: Printer): Unit = {
    println("ProtoLinkDirective")
    Option(node.attributes.value("message")).foreach { msg =>
      printer.println().print(s"Information about ${msg}")
    }
  }
}
