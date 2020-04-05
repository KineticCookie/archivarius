package com.lightbend.paradox.markdown.directives.standard

import java.io.File

import com.lightbend.paradox.markdown._

trait GitHubResolver {

  def variables: Map[String, String]

  val IssuesLink = """([^/]+/[^/]+)?#([0-9]+)""".r
  val CommitLink = """(([^/]+/[^/]+)?@)?(\p{XDigit}{5,40})""".r
  val TreeUrl    = """(.*github.com/[^/]+/[^/]+/tree/[^/]+)""".r
  val ProjectUrl = """(.*github.com/[^/]+/[^/]+).*""".r

  val baseUrl = PropertyUrl(GitHubResolver.baseUrl, variables.get)

  protected def resolvePath(page: Page, source: String, labelOpt: Option[String]): Url = {
    val pathUrl = Url.parse(source, "path is invalid")
    val path    = pathUrl.base.getPath
    val root = variables.get("github.root.base_dir") match {
      case None      => throw Url.Error("[github.root.base_dir] is not defined")
      case Some(dir) => new File(dir)
    }
    val file = path match {
      case p if p.startsWith(Path.toUnixStyleRootPath(root.getAbsolutePath)) => new File(p)
      case p if p.startsWith("/")                                            => new File(root, path.drop(1))
      case p                                                                 => new File(page.file.getParentFile, path)
    }
    val labelFragment =
      for {
        label      <- labelOpt
        (min, max) <- Snippet.extractLabelRange(file, label)
      } yield {
        if (min == max)
          s"L$min"
        else
          s"L$min-L$max"
      }
    val fragment = labelFragment.getOrElse(pathUrl.base.getFragment)
    val treePath = Path.relativeLocalPath(root.getAbsolutePath, file.getAbsolutePath)

    (treeUrl / treePath) withFragment fragment
  }

  protected def resolveProject(project: String) = {
    Option(project) match {
      case Some(path) => Url("https://github.com") / path
      case None       => projectUrl
    }
  }

  protected def projectUrl = baseUrl.collect {
    case ProjectUrl(url) => url
    case _               => throw Url.Error(s"[${GitHubResolver.baseUrl}] is not a project URL")
  }

  protected def treeUrl = baseUrl.collect {
    case TreeUrl(url)    => url
    case ProjectUrl(url) => url + "/tree/master"
    case _               => throw Url.Error(s"[${GitHubResolver.baseUrl}] is not a project or versioned tree URL")
  }

}

object GitHubResolver {
  val baseUrl = "github.base_url"
}
