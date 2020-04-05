package io.hydrosphere.archivarius.util

import cats.effect.{Resource, Sync}

import scala.io.{BufferedSource, Source}

object Resources {
  def sourceFromFile[F[_]](path: String)(implicit F: Sync[F]): Resource[F, BufferedSource] = {
    Resource.make(F.delay(Source.fromFile(path)))(x => F.delay(x.close()))
  }

}
