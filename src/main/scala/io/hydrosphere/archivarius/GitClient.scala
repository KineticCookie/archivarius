package io.hydrosphere.archivarius

import cats.Monad
import cats.implicits._
import io.chrisdavenport.log4cats.Logger
import io.hydrosphere.archivarius.util.ProcessRunner

trait GitClient[F[_]] {
  def clone(repo: String, target: String): F[Unit]
}

object GitClient {
  def cliClient[F[_]]()(
      implicit F: Monad[F],
      logger: Logger[F],
      processRunner: ProcessRunner[F]
  ): F[GitClient[F]] = {
    for {
      version <- processRunner.run("git --version").map(_.trim)
      _       <- logger.info(s"Using cli git client ($version)")
    } yield new GitClient[F] {
      override def clone(repo: String, target: String): F[Unit] = {
        val cmd = s"git clone $repo $target"
        processRunner.run(cmd).void
      }
    }
  }
}
