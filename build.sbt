lazy val paradox = project.in(file("paradox"))

lazy val archivarius = project
  .in(file("."))
  .dependsOn(paradox)
  .settings(
    name := "archivarius",
    version := "0.1.0",
    organization := "io.hydrosphere",
    scalaVersion := "2.13.1",
    Compile / scalacOptions ++= Seq(
      "-unchecked",
      "-deprecation",
      "-feature",
      "-language:existentials",
      "-language:higherKinds",
      "-language:implicitConversions",
      "-language:postfixOps",
      "-Ymacro-annotations",
      "-P:bm4:no-filtering:y"
    ),
    publishArtifact := false,
    parallelExecution in Test := false,
    parallelExecution in IntegrationTest := false,
    fork in (Test, test) := true,
    fork in (IntegrationTest, test) := true,
    fork in (IntegrationTest, testOnly) := true,
    libraryDependencies ++= Dependencies.all,
    autoCompilerPlugins := true,
    addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1"),
    mainClass := Some("io.hydrosphere.archivarius.Main")
  )
