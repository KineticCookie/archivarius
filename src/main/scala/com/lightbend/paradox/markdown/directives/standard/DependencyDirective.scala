package com.lightbend.paradox.markdown.directives.standard

import com.lightbend.paradox.markdown.directives.LeafBlockDirective
import org.pegdown.Printer
import org.pegdown.ast.{DirectiveNode, TextNode, Visitor}
import scala.jdk.CollectionConverters._

/**
  * Dependency directive.
  */
case class DependencyDirective(variables: Map[String, String])
    extends LeafBlockDirective("dependency") {
  val ScalaVersion       = variables.get("scala.version")
  val ScalaBinaryVersion = variables.get("scala.binary.version")

  def render(node: DirectiveNode, visitor: Visitor, printer: Printer): Unit = {
    node.contentsNode.getChildren.asScala.headOption match {
      case Some(text: TextNode) => renderDependency(text.getText, node, printer)
      case _                    => node.contentsNode.accept(visitor)
    }
  }

  def renderDependency(tools: String, node: DirectiveNode, printer: Printer): Unit = {
    val classes = Seq("dependency", node.attributes.classesString).filter(_.nonEmpty)

    val dependencyPostfixes = node.attributes
      .keys()
      .asScala
      .toSeq
      .filter(_.startsWith("group"))
      .sorted
      .map(_.replace("group", ""))

    val startDelimiter = node.attributes.value("start-delimiter", "$")
    val stopDelimiter  = node.attributes.value("stop-delimiter", "$")

    def coordinate(name: String): Option[String] =
      Option(node.attributes.value(name)).map { value =>
        variables.foldLeft(value) {
          case (str, (key, value)) =>
            str.replace(startDelimiter + key + stopDelimiter, value)
        }
      }

    def requiredCoordinate(name: String): String =
      coordinate(name).getOrElse(throw DependencyDirective.UndefinedVariable(name))

    def sbt(
        group: String,
        artifact: String,
        version: String,
        scope: Option[String],
        classifier: Option[String]
    ): String = {
      val scopeString = scope.map {
        case s @ ("runtime" | "compile" | "test") => " % " + s.capitalize
        case s                                    => s""" % "$s""""
      }
      val classifierString = classifier.map(" classifier " + '"' + _ + '"')
      val extra            = (scopeString ++ classifierString).mkString
      (ScalaVersion, ScalaBinaryVersion) match {
        case (Some(scalaVersion), _) if artifact.endsWith("_" + scalaVersion) =>
          val strippedArtifact = artifact.substring(0, artifact.length - 1 - scalaVersion.length)
          s""""$group" % "$strippedArtifact" % "$version"$extra cross CrossVersion.full"""

        case (_, Some(scalaBinVersion)) if artifact.endsWith("_" + scalaBinVersion) =>
          val strippedArtifact = artifact.substring(0, artifact.length - 1 - scalaBinVersion.length)
          s""""$group" %% "$strippedArtifact" % "$version"$extra"""

        case _ =>
          s""""$group" % "$artifact" % "$version"$extra"""
      }
    }

    def gradle(
        group: String,
        artifact: String,
        version: String,
        scope: Option[String],
        classifier: Option[String]
    ): String = {
      val conf  = scope.getOrElse("compile")
      val extra = classifier.map(c => s", classifier: '$c'").getOrElse("")
      s"""$conf group: '$group', name: '$artifact', version: '$version'$extra""".stripMargin
    }

    def mvn(
        group: String,
        artifact: String,
        version: String,
        scope: Option[String],
        classifier: Option[String]
    ): String = {
      val elements =
        Seq("groupId"                 -> group, "artifactId"     -> artifact, "version" -> version) ++
          classifier.map("classifier" -> _) ++ scope.map("scope" -> _)
      elements
        .map {
          case (element, value) => s"  <$element>$value</$element>"
        }
        .mkString("<dependency>\n", "\n", "\n</dependency>")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
    }

    printer.print(s"""<dl class="${classes.mkString(" ")}">""")
    tools.split("[,]").map(_.trim).filter(_.nonEmpty).foreach { tool =>
      val (lang, code) = tool match {
        case "sbt" =>
          val artifacts = dependencyPostfixes.map { dp =>
            sbt(
              requiredCoordinate(s"group$dp"),
              requiredCoordinate(s"artifact$dp"),
              requiredCoordinate(s"version$dp"),
              coordinate(s"scope$dp"),
              coordinate(s"classifier$dp")
            )
          }

          val libraryDependencies = artifacts match {
            case Seq(artifact) => s"libraryDependencies += $artifact"
            case artifacts =>
              Seq("libraryDependencies ++= Seq(", artifacts.map(a => s"  $a").mkString(",\n"), ")")
                .mkString("\n")
          }

          ("scala", libraryDependencies)

        case "gradle" | "Gradle" =>
          val artifacts = dependencyPostfixes.map { dp =>
            gradle(
              requiredCoordinate(s"group$dp"),
              requiredCoordinate(s"artifact$dp"),
              requiredCoordinate(s"version$dp"),
              coordinate(s"scope$dp"),
              coordinate(s"classifier$dp")
            )
          }

          val libraryDependencies =
            Seq("dependencies {", artifacts.map(a => s"  $a").mkString(",\n"), "}").mkString("\n")

          ("gradle", libraryDependencies)

        case "maven" | "Maven" | "mvn" =>
          val artifacts = dependencyPostfixes.map { dp =>
            mvn(
              requiredCoordinate(s"group$dp"),
              requiredCoordinate(s"artifact$dp"),
              requiredCoordinate(s"version$dp"),
              coordinate(s"scope$dp"),
              coordinate(s"classifier$dp")
            )
          }

          ("xml", artifacts.mkString("\n"))
      }

      printer.print(s"""<dt>$tool</dt>""")
      printer.print(s"""<dd>""")
      printer.print(s"""<pre class="prettyprint"><code class="language-$lang">$code</code></pre>""")
      printer.print(s"""</dd>""")
    }
    printer.print("""</dl>""")
  }
}

object DependencyDirective {

  case class UndefinedVariable(name: String) extends RuntimeException(s"'$name' is not defined")

}
