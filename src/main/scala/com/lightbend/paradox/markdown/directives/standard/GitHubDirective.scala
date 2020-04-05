package com.lightbend.paradox.markdown.directives.standard

import com.lightbend.paradox.markdown.{Page, Url}
import org.pegdown.ast.DirectiveNode

/**
  * GitHub directive.
  *
  * Link to GitHub project entities like issues, commits and source code.
  * Supports most of the references documented in:
  * https://help.github.com/articles/autolinked-references-and-urls/
  */
case class GitHubDirective(page: Page, variables: Map[String, String])
    extends ExternalLinkDirective("github", "github:")
    with GitHubResolver {

  def resolveLink(node: DirectiveNode, link: String): Url = {
    link match {
      case IssuesLink(project, issue)     => resolveProject(project) / "issues" / issue
      case CommitLink(_, project, commit) => resolveProject(project) / "commit" / commit
      case path                           => resolvePath(page, path, Option(node.attributes.identifier()))
    }
  }

}
