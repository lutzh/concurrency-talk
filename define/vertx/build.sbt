name := "vertx-define"

version := "0.1"

javacOptions ++= Seq("-source", "1.8", "-target", "1.8")

libraryDependencies ++= Seq(
  "com.typesafe" % "config" % "1.3.0",
  "io.vertx" % "vertx-core" % "3.1.0",
  "org.apache.commons" % "commons-lang3" % "3.4"
)
