package com.lightbend.paradox.markdown.directives.standard

import java.io.FileNotFoundException

import com.lightbend.paradox.markdown.directives.{LeafBlockDirective, SourceDirective}
import com.lightbend.paradox.markdown.{Page, Snippet}
import org.pegdown.Printer
import org.pegdown.ast.{DirectiveNode, Visitor}
import scala.jdk.CollectionConverters._

/**
  * Fiddle directive.
  *
  * Extracts fiddles from source files into fiddle blocks.
  */
case class FiddleDirective(page: Page, variables: Map[String, String])
    extends LeafBlockDirective("fiddle")
    with SourceDirective {

  def render(node: DirectiveNode, visitor: Visitor, printer: Printer): Unit = {
    try {
      val labels = node.attributes.values("identifier").asScala.toList

      val integrationScriptUrl =
        node.attributes.value("integrationScriptUrl", "https://embed.scalafiddle.io/integration.js")

      // integration params as listed here:
      // https://github.com/scalafiddle/scalafiddle-core/tree/master/integrations#scalafiddle-integration
      // 'selector' is excluded on purpose to not complicate logic and increase maintainability
      val validParams =
        Seq("prefix", "dependency", "scalaversion", "template", "theme", "minheight", "layout")

      val params = validParams
        .map(k =>
          Option(node.attributes.value(k))
            .map { x =>
              if (x.startsWith("'") && x.endsWith("'")) // earlier explicit ' was required to quote attributes (now all are quoted with ")
                s"""data-$k="${x.substring(1, x.length - 1)}" """
              else
                s"""data-$k="$x" """
            }
            .getOrElse("")
        )
        .mkString(" ")

      val source = resolvedSource(node, page)
      val file   = resolveFile("fiddle", source, page, variables)
      val filterLabels = node.attributes
        .booleanValue("filterLabels", variables.get("fiddle.filterLabels").forall(_ == "true"))
      val (code, _) = Snippet(file, labels, filterLabels)

      printer.println.print(s"""
        <div data-scalafiddle="true" $params>
          <pre class="prettyprint"><code class="language-scala">$code</code></pre>
        </div>
        <script defer="true" src="$integrationScriptUrl"></script>
        """)
    } catch {
      case e: FileNotFoundException =>
        throw new FiddleDirective.LinkException(
          s"Unknown fiddle [${e.getMessage}] referenced from [${page.path}]"
        )
    }
  }
}

object FiddleDirective {

  /**
    * Exception thrown for unknown snip links.
    */
  class LinkException(message: String) extends RuntimeException(message)

}
