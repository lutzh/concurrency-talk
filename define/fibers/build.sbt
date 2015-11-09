name := "fibers-define"

version := "0.1"

// Ensure we run outside of sbt. This is especially useful for setting JVM-level flags
fork in run := true

// Set flags for java. Memory, GC settings, properties, etc.
javaOptions ++= Seq(s"-javaagent:" + Path.userHome.absolutePath + "/.ivy2/cache/co.paralleluniverse/quasar-core/jars/quasar-core-0.7.3-jdk8.jar")

javacOptions ++= Seq("-source", "1.8", "-target", "1.8")

libraryDependencies ++= Seq(
  "com.typesafe" % "config" % "1.3.0",
  "co.paralleluniverse" % "quasar-core" % "0.7.3" classifier "jdk8",
  "co.paralleluniverse" % "comsat-httpclient" % "0.5.0",
  "org.apache.commons" % "commons-lang3" % "3.4"
)
