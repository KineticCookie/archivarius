package io.hydrosphere.archivarius.proto.ast

import io.hydrosphere.archivarius.proto.plugin_schema.RawProtoSchema

trait ProtoType

object ProtoField {
  case class Scalar(name: String, description: String) extends ProtoType

  case class Message() extends ProtoType

  case class Enum(name: String, `package`: String) extends ProtoType
}

object ProtoAst {
  def mk(rawProtoSchema: RawProtoSchema) = {}
}
