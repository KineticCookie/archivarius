import sbt._

object Dependencies {

  val logs = Seq(
    "ch.qos.logback"    % "logback-classic" % "1.2.3",
    "io.chrisdavenport" %% "log4cats-core"  % "1.0.1",
    "io.chrisdavenport" %% "log4cats-slf4j" % "1.0.1"
  )

  val cats = Seq(
    "org.typelevel" %% "cats-core"   % "2.1.1",
    "org.typelevel" %% "cats-effect" % "2.1.2",
    "co.fs2"        %% "fs2-core"    % "2.2.1",
    "co.fs2"        %% "fs2-io"      % "2.2.1"
  )

  val circeVersion = "0.12.3"
  val circe = Seq(
    "io.circe" %% "circe-core",
    "io.circe" %% "circe-generic",
    "io.circe" %% "circe-parser"
  ).map(_ % circeVersion)

  val test = Seq(
    "org.scalactic" %% "scalactic" % "3.1.1",
    "org.scalatest" %% "scalatest" % "3.1.1" % "test"
  )

  val paradox = Seq(
    "org.pegdown"   % "pegdown"        % "1.6.0",
    "org.parboiled" % "parboiled-java" % "1.3.0", // overwrite for JDK10 support
    "org.antlr"     % "ST4"            % "4.1"
  )

  val all = cats ++ circe ++ logs ++ paradox ++ test
}
