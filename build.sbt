
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
  "-encoding", "UTF-8",
 "-Xdisable-assertions"
)

//scalacOptions in Runtime ++= Seq(
//  //"-Xdisable-assertions"
//  "-J-da"
//)

javaOptions in Universal ++= Seq(
  "-Dfile.encoding=UTF-8",
  "-da"
)

mainClass in Compile := Some("Example")

lazy val blog = (project in file(".")).
  enablePlugins(JavaAppPackaging).
  settings(commonSettings: _*)