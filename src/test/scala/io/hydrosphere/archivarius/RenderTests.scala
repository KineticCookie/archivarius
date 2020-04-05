package io.hydrosphere.archivarius

import java.io.File
import java.nio.file.{Files, Paths}

import com.lightbend.paradox.ParadoxProcessor
import com.lightbend.paradox.template.PageTemplate
import org.stringtemplate.v4.STErrorListener
import org.stringtemplate.v4.misc.STMessage

class RenderTests extends UnitSpec {

  def getMappings(folder: String): List[(File, String)] = {
    def recursiveListFiles(f: File): List[(File, String)] = {
      val all = f.listFiles
      val files =
        all.filter(_.isFile).toList.collect {
          case f: File if f.getName.endsWith(".md") =>
            f -> Paths.get(folder).relativize(f.toPath).toString
        }
      val directories = all.filter(_.isDirectory)
      files ++
        directories
          .flatMap(recursiveListFiles)
    }

    recursiveListFiles(new File(folder))
  }

  test("Paradox renderer") {
    val path     = "/Users/blutfullin/dev/serving/root/docs/src/main/paradox"
    val mappings = getMappings(path)
    println("Mappings")
    mappings.foreach(println)

    val outputPath = Files.createDirectories(Paths.get("target/paradox"))

    val properties = Map(
      "project.name"    -> "test",
      "project.version" -> "0.1.0",
      "image.base_url"  -> "http://localhost"
    )

    val renderer = new ParadoxProcessor()
    val pageTemplate =
      new PageTemplate(
        new File("/Users/blutfullin/dev/serving/root/docs/src/main/paradox/_template")
      )
    val errorListener = new STErrorListener {
      override def compileTimeError(msg: STMessage): Unit = println(msg)

      override def runTimeError(msg: STMessage): Unit = println(msg)

      override def IOError(msg: STMessage): Unit = println(msg)

      override def internalError(msg: STMessage): Unit = println(msg)
    }
    renderer.process(
      mappings = mappings,
      leadingBreadcrumbs = Nil,
      outputDirectory = outputPath.toFile,
      sourceSuffix = ".md",
      targetSuffix = ".html",
      groups = Map.empty,
      properties = properties,
      navDepth = 1,
      navExpandDepth = None,
      navIncludeHeaders = true,
      pageTemplate = pageTemplate,
      errorListener = errorListener
    )
  }
}
