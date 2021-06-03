name := "blog-bytecode"

version := "1.0"

scalaVersion := "2.12.4"

libraryDependencies ++= {
  val circeV     = "0.11.1"

  Seq(
    "org.scalatest" %% "scalatest" % "3.0.3" % "test",
    "io.circe" %% "circe-core" % circeV,
    "io.circe" %% "circe-generic" % circeV,
    "io.circe" %% "circe-parser" % circeV,
    "io.circe" %% "circe-jawn" % circeV,
    "io.circe" %% "circe-optics" % "0.11.0"
  )
}

scalacOptions ++= Seq(
  "-target:jvm-1.8",
  "-encoding", "UTF-8",
  "-deprecation", "-feature",
  "-language:implicitConversions",
  "-language:higherKinds",
  "-language:existentials",
  "-language:postfixOps",
  "-Ywarn-adapted-args",
  "-Ywarn-dead-code",
  "-Ywarn-unused",
  "-Ywarn-inaccessible",
  "-Ywarn-value-discard" ,
  "-Ywarn-unused-import",
  "-unchecked")