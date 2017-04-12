name := "dsChallengeSbt"

version := "1.0"

scalaVersion := "2.12.1"

val json4sNative = "org.json4s" %% "json4s-native" % "3.5.1"
val json4sJackson = "org.json4s" %% "json4s-jackson" % "3.5.1"

libraryDependencies += json4sNative
libraryDependencies += json4sJackson

libraryDependencies += "org.scalaz" %% "scalaz-core" % "7.2.10"

libraryDependencies ++= Seq(
  "org.apache.lucene" % "lucene-core" % "6.5.0",
  "org.apache.lucene" % "lucene-analyzers-common" % "6.5.0",
  "org.apache.lucene" % "lucene-queries" % "6.5.0",
  "org.apache.lucene" % "lucene-queryparser" % "6.5.0"
)

