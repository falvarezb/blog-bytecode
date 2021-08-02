import scala.sys.process.Process

lazy val commonSettings = Seq(
name := "blog-bytecode",
version := "1.0",
scalaVersion := "2.12.4"
)
libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.0.3" % "test"
)

scalacOptions ++= Seq(
  "-target:jvm-1.8",
  "-encoding", "UTF-8"
)

javaOptions in Universal ++= Seq(
  "-Dfile.encoding=UTF-8"
)

mainClass in Compile := Some("Example")

lazy val blog = (project in file(".")).
  enablePlugins(JavaAppPackaging).
  settings(commonSettings: _*)

val gitHeadCommitSha = taskKey[String]( "Determines the current git commit SHA")
gitHeadCommitSha := Process("git rev-parse HEAD").lineStream.head

val makeVersionProperties = taskKey[Seq[File]]("Makes a version.properties file.")
makeVersionProperties := {
  val propFile = new File((resourceManaged in Compile).value, "version.properties")
  val content = s"version=${gitHeadCommitSha.value}"
  IO.write(propFile, content)
  Seq(propFile)
}

resourceGenerators in Compile += makeVersionProperties