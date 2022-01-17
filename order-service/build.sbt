name := "order-service"

version := "0.1"

scalacOptions ++= Seq(
  "-deprecation",
  "-feature",
  "-Xfatal-warnings"
)
val sparkVersion          = "2.4.3"
val vegasVersion          = "0.3.11"
val postgresVersion       = "42.2.2"
val http4sVersion         = "0.21.7"
val doobieVersion         = "0.9.0"
val catsVersion           = "2.2.0"
val catsTaglessVersion    = "0.11"
val catsEffectVersion     = "2.2.0"
val catsScalacheckVersion = "0.2.0"
val enumeratumVersion     = "1.6.1"

scalaVersion := "2.12.10"

val circeVersion       = "0.14.0"
val circeConfigVersion = "0.8.0"

libraryDependencies ++= Seq(
  "ch.qos.logback"  % "logback-classic"        % "1.2.3",
  "io.circe"       %% "circe-config"           % circeConfigVersion,
  "io.circe"       %% "circe-core"             % circeVersion,
  "io.circe"       %% "circe-generic"          % circeVersion,
  "io.circe"       %% "circe-generic-extras"   % circeVersion,
  "io.circe"       %% "circe-optics"           % circeVersion,
  "io.circe"       %% "circe-parser"           % circeVersion,
  "org.typelevel"  %% "cats-core"              % catsVersion,
  "org.typelevel"  %% "cats-effect"            % catsEffectVersion,
  "org.http4s"     %% "http4s-dsl"             % http4sVersion,
  "org.http4s"     %% "http4s-blaze-server"    % http4sVersion,
  "org.http4s"     %% "http4s-blaze-client"    % http4sVersion,
  "org.http4s"     %% "http4s-circe"           % http4sVersion,
  "org.http4s"     %% "http4s-jdk-http-client" % "0.3.6",
  "org.tpolecat"   %% "doobie-core"            % doobieVersion,
  "org.tpolecat"   %% "doobie-h2"              % doobieVersion,
  "org.tpolecat"   %% "doobie-hikari"          % doobieVersion,
  "org.tpolecat"   %% "doobie-postgres"        % doobieVersion,
  "org.flywaydb"    % "flyway-core"            % "6.2.4",
  "dev.profunktor" %% "http4s-jwt-auth"        % "0.0.7",
  "io.circe"       %% "circe-config"           % circeConfigVersion,
  "io.circe"       %% "circe-core"             % circeVersion,
  "io.circe"       %% "circe-generic"          % circeVersion,
  "io.circe"       %% "circe-generic-extras"   % circeVersion,
  "io.circe"       %% "circe-optics"           % circeVersion,
  "io.circe"       %% "circe-parser"           % circeVersion,
  "com.beachape"   %% "enumeratum"             % enumeratumVersion,
  "com.beachape"   %% "enumeratum-circe"       % enumeratumVersion,
  "org.typelevel"  %% "cats-tagless-macros"    % catsTaglessVersion
)

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % sparkVersion,
  "org.apache.spark" %% "spark-sql"  % sparkVersion,
  "org.postgresql"    % "postgresql" % postgresVersion
)
libraryDependencies ++= Seq(
  "org.apache.kafka"  % "kafka-clients"       % "2.8.0",
  "org.apache.kafka"  % "kafka-streams"       % "2.8.0",
  "org.apache.kafka" %% "kafka-streams-scala" % "2.8.0"
)
libraryDependencies ++= {
  val kafkaSerializationV = "0.5.0"
  Seq(
    "com.ovoenergy" %% "kafka-serialization-avro4s" % kafkaSerializationV // To provide Avro4s Avro support
  )
}
addCompilerPlugin(
  "org.typelevel" %% "kind-projector" % "0.13.0" cross CrossVersion.full
)
addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)
