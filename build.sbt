organization := "co.torri"

name := "scala-freenode-search"

version := "1.0"

scalaVersion := "2.9.0-1"

seq(webSettings :_*)

libraryDependencies ++= Seq(
  "org.scalatra" %% "scalatra" % "2.0.0-SNAPSHOT",
  "org.scalatra" %% "scalatra-scalate" % "2.0.0-SNAPSHOT",
  "javax.servlet" % "servlet-api" % "2.5" % "provided",
  "org.eclipse.jetty" % "jetty-webapp" % "7.4.0.RC0" % "jetty",
  "org.apache.lucene" % "lucene-core" % "3.3.0",
  "se.scalablesolutions.akka" % "akka-actor" % "1.1.3",
  "pircbot" % "pircbot" % "1.5.0"
)

libraryDependencies ++= Seq(
  "org.specs2" %% "specs2" % "1.4" % "test",
  "org.specs2" %% "specs2-scalaz-core" % "6.0.RC2" % "test",
  "org.mockito" % "mockito-all" % "1.8.5" % "test",
  "se.scalablesolutions.akka" % "akka-testkit" % "1.1.3" % "test"
)

resolvers += "Sonatype OSS Snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/"

resolvers += "Akka Maven2 Repository" at "http://akka.io/repository/"

unmanagedBase <<= baseDirectory { base => base / "custom_lib" }
