enablePlugins(JavaAppPackaging)

name := "reactive-life"

version := "0.1.0"

scalaVersion := "2.12.8"

lazy val akkaVersion = "2.5.22"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "de.h2b.scala.lib" %% "simgraf" % "1.3.0" // https://gitlab.com/h2b/SimGraf
)

//javaOptions in Universal ++= Seq(
//  "-J-Xloggc:gc.log",
//  "-J-XX:+PrintGCCause",
//  "-J-XX:+PrintGCDetails",
//  "-J-XX:+PrintGCDateStamps",
//  "-J-XX:+PrintTenuringDistribution"
//)
