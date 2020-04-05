package io.hydrosphere.archivarius

import cats.effect.IO
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

abstract class UnitSpec extends AnyFunSuite with Matchers {
  implicit val logger = Slf4jLogger.fromClass[IO](this.getClass).unsafeRunSync()

}
