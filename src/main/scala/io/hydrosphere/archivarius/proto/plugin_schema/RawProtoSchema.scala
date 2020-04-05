/**
  * Low level parser of a proto schema.
  *
  * Schema is created by protoc compiler plugin https://github.com/pseudomuto/protoc-gen-doc
  */
package io.hydrosphere.archivarius.proto.plugin_schema

import cats.effect.Sync
import cats.implicits._
import io.chrisdavenport.log4cats.Logger
import io.circe.generic.JsonCodec
import io.circe.parser._
import io.hydrosphere.archivarius.util.Resources

@JsonCodec case class RawProtoSchema(
    files: List[RawProtoFile],
    scalarValueTypes: List[RawProtoScalarType]
)

@JsonCodec case class RawProtoFile(
    name: String,
    description: String,
    `package`: String,
    enums: List[RawProtoEnum],
    messages: List[RawProtoMessage],
    services: List[RawProtoService]
//    extensions: List[ProtoExtension]  // TODO(bulat): implement extensions later
)

@JsonCodec case class RawProtoMessage(
    name: String,
    longName: String,
    fullName: String,
    description: String,
//    extensions: List[ProtoExtension],  // TODO(bulat): implement extensions later
    fields: List[RawProtoField]
)

@JsonCodec case class RawProtoField(
    name: String,
    description: String,
    label: String,
    `type`: String,
    longType: String,
    fullType: String,
    defaultValue: String,
    ismap: Boolean
)

@JsonCodec case class RawProtoMethod(
    name: String,
    description: String,
    requestType: String,
    requestLongType: String,
    requestFullType: String,
    requestStreaming: Boolean,
    responseType: String,
    responseLongType: String,
    responseFullType: String,
    responseStreaming: Boolean
)

@JsonCodec case class RawProtoService(
    name: String,
    longName: String,
    fullName: String,
    description: String,
    methods: List[RawProtoMethod]
)

@JsonCodec case class RawProtoEnumValue(name: String, number: String, description: String)

@JsonCodec case class RawProtoEnum(
    name: String,
    longName: String,
    fullName: String,
    description: String,
    values: List[RawProtoEnumValue]
)

@JsonCodec case class RawProtoScalarType(
    protoType: String,
    notes: String,
    cppType: String,
    csType: String,
    goType: String,
    javaType: String,
    phpType: String,
    pythonType: String,
    rubyType: String
)

object RawProtoSchema {
  def readJson[F[_]](path: String)(implicit F: Sync[F], logger: Logger[F]): F[RawProtoSchema] = {
    for {
      jsonText <- Resources.sourceFromFile(path).use(x => F.delay(x.mkString))
      _        <- logger.debug(s"Read ${path}")
      json     <- F.fromEither(parse(jsonText))
      _        <- logger.debug(s"Parsed json ${path}")
      schema   <- F.fromEither(json.as[RawProtoSchema])
      _        <- logger.debug(s"Converted to raw schema ${path}")
    } yield schema
  }
}
