name := "trc"

version := "1.0.0"
scalaVersion := "2.12.9"

lazy val akkaVersion = "2.5.23"
lazy val akkaHttpVersion = "10.1.9"

enablePlugins(JavaAppPackaging)

dockerExposedPorts := Seq( 9000, 2552 )
val AkkaHttpJsonVersion = "1.34.0"
// Dependencies
libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http"   % akkaHttpVersion,
  "com.roundeights" %% "hasher" % "1.2.0",

  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "com.typesafe.akka" %% "akka-remote" % akkaVersion,
  "com.typesafe.akka" %% "akka-persistence-typed" % akkaVersion,
  "de.heikoseeberger" %% "akka-http-json4s" % "1.16.1",
  "org.json4s" %% "json4s-native" % "3.5.2")

val circeVersion = "0.14.1"


mainClass := Some("Application")