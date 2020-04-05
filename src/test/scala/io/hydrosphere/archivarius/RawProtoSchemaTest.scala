package io.hydrosphere.archivarius

import cats.effect.IO
import io.hydrosphere.archivarius.proto.plugin_schema.RawProtoSchema

class RawProtoSchemaTest extends UnitSpec {
  test("ProtoSchema should be parsed from json") {
    val rawProtoSchema = RawProtoSchema.readJson[IO]("examples/schema.json").unsafeRunSync()
    println(s"${rawProtoSchema.files.length} files parsed")
  }
}
