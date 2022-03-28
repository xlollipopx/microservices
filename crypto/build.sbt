import com.typesafe.sbt.packager.docker.DockerPermissionStrategy

name := "crypto"

version      := "1.0.0"
scalaVersion := "2.12.9"

lazy val akkaVersion     = "2.6.8"
lazy val akkaHttpVersion = "10.2.9"

enablePlugins(JavaAppPackaging)

dockerExposedPorts := Seq(9000, 2552)
val AkkaHttpJsonVersion = "1.34.0"
// Dependencies
libraryDependencies ++= Seq(
  "com.typesafe.akka"          %% "akka-http"              % akkaHttpVersion,
  "com.roundeights"            %% "hasher"                 % "1.2.0",
  "com.typesafe.akka"          %% "akka-remote"            % akkaVersion,
  "com.typesafe.akka"          %% "akka-persistence-typed" % akkaVersion,
  "de.heikoseeberger"          %% "akka-http-json4s"       % "1.16.1",
  "org.json4s"                 %% "json4s-native"          % "3.5.2",
  "com.typesafe.scala-logging" %% "scala-logging"          % "3.5.0",
  "ch.megard"                  %% "akka-http-cors"         % "1.1.3"
)
libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.1.3" % Runtime

libraryDependencies ++= Seq(
  "org.scalacheck"    %% "scalacheck"                  % "1.13.4",
  "org.scalatest"     %% "scalatest"                   % "3.0.1",
  "org.scalamock"     %% "scalamock-scalatest-support" % "3.6.0",
  "com.typesafe.akka" %% "akka-stream-testkit"         % akkaVersion,
  "com.typesafe.akka" %% "akka-http-testkit"           % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-testkit"                % akkaVersion,
  "org.bouncycastle"   % "bcprov-jdk15on"              % "1.64",
  "commons-codec"      % "commons-codec"               % "1.14"
).map(_ % "test")

libraryDependencies += "com.github.jwt-scala" %% "jwt-json4s-native" % "8.0.0"

val circeVersion = "0.14.1"
//dockerPermissionStrategy := DockerPermissionStrategy.None

mainClass := Some("Application")
