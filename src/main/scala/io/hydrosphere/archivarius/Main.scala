package io.hydrosphere.archivarius

import cats.effect.{ExitCode, IO, IOApp}
import io.chrisdavenport.log4cats.Logger
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import io.hydrosphere.archivarius.util.ProcessRunner

object Main extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = {
    for {
      implicit0(logger: Logger[IO]) <- Slf4jLogger.create[IO]
      implicit0(runner: ProcessRunner[IO]) = ProcessRunner.mkWithLogger[IO]()
      gitClient <- GitClient.cliClient[IO]()
    } yield ExitCode.Success
  }
}
