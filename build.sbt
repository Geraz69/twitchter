name := "twitchter"

version := "1.0"

scalaVersion := "2.12.1"

resolvers += Resolver.sonatypeRepo("releases")

libraryDependencies += "com.danielasfregola" %% "twitter4s" % "5.1"

libraryDependencies += "com.typesafe.akka" %% "akka-persistence" % "2.4.16"

libraryDependencies += "com.safety-data" %% "akka-persistence-redis" % "0.1.0"
