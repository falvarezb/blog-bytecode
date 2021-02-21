name := "blog-bytecode"

version := "1.0"

scalaVersion := "2.12.4"

val scalaLoggingV = "3.8.0"
val logbackV = "1.2.3"

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.0.3" % "test",
  "com.typesafe.scala-logging" %% "scala-logging" % scalaLoggingV,
  "ch.qos.logback" % "logback-classic" % logbackV
)

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