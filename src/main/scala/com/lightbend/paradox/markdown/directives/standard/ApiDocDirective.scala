package com.lightbend.paradox.markdown.directives.standard

import com.lightbend.paradox.markdown.{Page, PropertyUrl, Url}
import org.pegdown.ast.DirectiveNode

/**
  * API doc directive.
  *
  * Link to javadoc and scaladoc based on package prefix. Will match the
  * configured base URL with the longest package prefix. For example,
  * given:
  *
  * - `scaladoc.akka.base_url=http://doc.akka.io/api/akka/x.y.z`
  * - `scaladoc.akka.http.base_url=http://doc.akka.io/api/akka-http/x.y.z`
  *
  * Then `@scaladoc[Http](akka.http.scaladsl.Http)` will match the latter.
  */
abstract class ApiDocDirective(name: String, page: Page, variables: Map[String, String])
    extends ExternalLinkDirective(name, name + ":") {

  def resolveApiLink(base: Url, link: String): Url

  val defaultBaseUrl = PropertyUrl(name + ".base_url", variables.get)
  val ApiDocProperty = raw"""$name\.(.*)\.base_url""".r
  val baseUrls = variables.collect {
    case (property @ ApiDocProperty(pkg), url) => (pkg, PropertyUrl(property, variables.get))
  }

  def resolveLink(node: DirectiveNode, link: String): Url = {
    val levels       = link.split("[.]")
    val packages     = (1 to levels.init.size).map(levels.take(_).mkString("."))
    val baseUrl      = packages.reverse.collectFirst(baseUrls).getOrElse(defaultBaseUrl).resolve()
    val resolvedLink = resolveApiLink(baseUrl, link)
    val resolvedPath = resolvedLink.base.getPath
    if (resolvedPath startsWith ".../") resolvedLink.copy(path = page.base + resolvedPath.drop(4))
    else resolvedLink
  }

}
