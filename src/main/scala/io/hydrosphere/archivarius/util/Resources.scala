package io.hydrosphere.archivarius.util

import java.io.File

import cats.effect.{Resource, Sync}

import scala.io.{BufferedSource, Source}

object Resources {
  def sourceFromPath[F[_]](path: String)(implicit F: Sync[F]): Resource[F, BufferedSource] = {
    Resource.make(F.delay(Source.fromFile(path)))(x => F.delay(x.close()))
  }

  def sourceFromFile[F[_]](file: File)(implicit F: Sync[F]): Resource[F, BufferedSource] = {
    Resource.make(F.delay(Source.fromFile(file)))(x => F.delay(x.close()))
  }

}
