package io.hydrosphere.archivarius.proto.compiler

import io.hydrosphere.archivarius.util.ProcessRunner

object ProtoCompiler {
  def compileProtos[F[_]](inputPath: String, outputPath: String, exclude: Option[String])(
      implicit runner: ProcessRunner[F]
  ) = {
    val excludePart = exclude.map(x => ":" + x).getOrElse("")
    val cmdStr =
      s"protoc --doc_out=$outputPath --doc_opt=json,schema.json$excludePart $inputPath"
    runner.run(cmdStr)
  }
}
