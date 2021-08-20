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

val ausername = scala.sys.env.getOrElse("ARTIFACTORY_USERNAME", "")

val mysetting = taskKey[String]("logging")

mysetting := {
  Keys.sLog.value.info(s"hoho $ausername")
  "hello"
}

val makeVersionProperties = taskKey[Seq[File]]("Makes a version.properties file.")
makeVersionProperties := {
  val propFile = new File((resourceManaged in Compile).value, "version.properties")
  val content = s"version=${gitHeadCommitSha.value}"
  IO.write(propFile, content)
  Seq(propFile)
}

resourceGenerators in Compile += makeVersionProperties

val dependentJarDirectory = settingKey[File]("location of the unpacked dependent jars")
dependentJarDirectory := target.value / "dependent-jars"

val createDependentJarDirectory = taskKey[File]("create the dependent-jars directory")
createDependentJarDirectory := {
  sbt.IO.createDirectory(dependentJarDirectory.value)
  dependentJarDirectory.value
}

val excludes = List("meta-inf", "license", "play.plugins","reference.conf")
//excludes files that match 'excludes' or already exist
def unpackFilter(target: File) = new NameFilter {
  def accept(name: String) = {
    !excludes.exists(x => name.toLowerCase().startsWith(x)) &&
      !file(target.getAbsolutePath + "/" + name).exists
  } }

def unpack(target: File, f: File, log: Logger) = {
  log.debug("unpacking " + f.getName)
  if (f.isDirectory) sbt.IO.copyDirectory(f, target)
  else sbt.IO.unzip(f, target, filter = unpackFilter(target))
}

//val unpackJars = taskKey[Seq[_]]("unpacks a dependent jars into target/dependent-jars")
//  unpackJars := {
//  Build.data((dependencyClasspath in Runtime).value).map ( f => unpack(dependentJarDirectory.value, f))
//}


val createUberJar = taskKey[File]("create jar which we will run")
createUberJar := {
  val output = target.value / "build.jar"
  create (dependentJarDirectory.value, output);
  output
}

def create(dir: File, buildJar: File) = {
  val files = (dir ** "*").get.filter(_ != dir)
  val filesWithPath = files.map(x => (x, x.relativeTo(dir).get.getPath))
  filesWithPath foreach println
  sbt.IO.zip(filesWithPath, buildJar)
}


