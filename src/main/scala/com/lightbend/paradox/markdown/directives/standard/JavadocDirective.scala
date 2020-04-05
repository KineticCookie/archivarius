package com.lightbend.paradox.markdown.directives.standard

import com.lightbend.paradox.markdown.{Page, Url}

case class JavadocDirective(page: Page, variables: Map[String, String])
    extends ApiDocDirective("javadoc", page, variables) {

  def resolveApiLink(baseUrl: Url, link: String): Url = {
    val url  = Url(link).base
    val path = url.getPath.replace('.', '/') + ".html"
    baseUrl.withEndingSlash.withQuery(path).withFragment(url.getFragment)
  }

}
