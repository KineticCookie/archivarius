# Archivarius

The documentation renderer based on [sbt-paradox](https://github.com/lightbend/paradox).

Since it's not supported anymore I decided to migrate it to scala 2.13 and improve by adding
new directives.

## Parsing proto schema

### Get ast from proto files
pwd is hydro-serving-protos repository

```
docker run -it --entrypoint bash \
  -v $(pwd)/doc:/out \
  -v $(pwd)/src/:/protos \
  pseudomuto/protoc-gen-doc

protoc --doc_out=/out --doc_opt=json,docs.json:hydro_serving_grpc/onnx/ $(find . -name '*.proto') 2> /out/warnings.txt
```