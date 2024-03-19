
name := "CSYE7200API"

version := "0.1"

scalaVersion := "2.13.12"

libraryDependencies ++= Seq(
  "com.lihaoyi" %% "requests" % "0.6.5",
  "io.circe" %% "circe-core" % "0.14.1",
  "io.circe" %% "circe-generic" % "0.14.1",
  "io.circe" %% "circe-parser" % "0.14.1"
)
libraryDependencies += "org.scalaj" %% "scalaj-http" % "2.4.2"

libraryDependencies += "com.typesafe.play" %% "play-json" % "2.10.0"



