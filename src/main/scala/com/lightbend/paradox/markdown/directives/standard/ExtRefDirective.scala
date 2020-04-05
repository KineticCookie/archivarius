package com.lightbend.paradox.markdown.directives.standard

import com.lightbend.paradox.markdown.{Page, PropertyUrl, Url}
import org.pegdown.ast.DirectiveNode

/**
  * ExtRef directive.
  *
  * Link to external pages using URL templates.
  */
case class ExtRefDirective(page: Page, variables: Map[String, String])
    extends ExternalLinkDirective("extref", "extref:") {

  def resolveLink(node: DirectiveNode, link: String): Url = {
    link.split(":", 2) match {
      case Array(scheme, expr) =>
        PropertyUrl(s"extref.$scheme.base_url", variables.get).format(expr)
      case _ => throw Url.Error("URL has no scheme")
    }
  }

}
