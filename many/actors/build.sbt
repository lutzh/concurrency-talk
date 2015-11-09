name := """actors"""

version := "0.1"

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.4.0",
  "com.typesafe.akka" %% "akka-testkit" % "2.4.0" % "test",
  "org.scalatest" %% "scalatest" % "2.2.4" % "test"
)

