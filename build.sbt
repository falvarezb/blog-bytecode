name := "blog-bytecode"

version := "1.0"

scalaVersion := "2.12.4"


libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.0.3" % "test",
  "org.typelevel" %% "cats-core" % "1.6.0",
  "org.typelevel" %% "cats-effect" % "1.2.0"
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
  "-unchecked",
  "-Ypartial-unification"
)