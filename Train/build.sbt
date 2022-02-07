name := "Train"
 
version := "1.0" 
      
lazy val `train` = (project in file(".")).enablePlugins(PlayScala)

      
resolvers += "Akka Snapshot Repository" at "https://repo.akka.io/snapshots/"
      
scalaVersion := "2.13.5"

libraryDependencies ++= Seq( jdbc , ehcache , ws , specs2 % Test , guice,
  "org.sangria-graphql" %% "sangria" % "2.0.1",
  "org.sangria-graphql" %% "sangria-slowlog" % "2.0.1",
  "org.sangria-graphql" %% "sangria-play-json" % "2.0.1",
  "org.scalatest" %% "scalatest" % "3.1.4" % "test")
      