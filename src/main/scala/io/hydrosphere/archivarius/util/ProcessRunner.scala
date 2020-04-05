package io.hydrosphere.archivarius.util

import cats.effect.Sync
import cats.implicits._

import sys.process._
import io.chrisdavenport.log4cats.Logger

import scala.collection.mutable.ListBuffer
import scala.util.control.NonFatal

trait ProcessRunner[F[_]] {
  def run(cmd: String): F[String]
}

object ProcessRunner {
  def mkWithLogger[F[_]]()(implicit F: Sync[F], logger: Logger[F]): ProcessRunner[F] =
    new ProcessRunner[F] {
      override def run(cmd: String): F[String] = {
        val processOut = ListBuffer.empty[String]
        val processLogger = new ProcessLogger {
          override def out(s: => String): Unit = processOut.addOne(s)

          override def err(s: => String): Unit = processOut.addOne(s)

          override def buffer[T](f: => T): T = f
        }
        val flow = for {
          _   <- logger.debug(s"[ProcessRunner]🚀: '${cmd}'")
          res <- F.delay(cmd.!!(processLogger))
          _   <- logger.debug(s"[ProcessRunner]✅: '${cmd}' exited: ${res.trim}")
        } yield res
        flow.onError {
          case ex =>
            logger.error(ex)(s"[ProcessRunner]❌: '${cmd}' failed: ${processOut.mkString}")
        }
      }
    }
}
