organization := "co.torri"

name := "scala-freenode-search"

version := "1.0"

scalaVersion := "2.9.0-1"

seq(webSettings :_*)

seq(sbtassembly.Plugin.assemblySettings: _*)

libraryDependencies ++= Seq(
  "org.scalatra" %% "scalatra" % "2.0.0-SNAPSHOT",
  "org.scalatra" %% "scalatra-scalate" % "2.0.0-SNAPSHOT",
  "javax.servlet" % "servlet-api" % "2.5" % "provided",
  "org.eclipse.jetty" % "jetty-webapp" % "7.4.0.RC0" % "jetty",
  "org.apache.lucene" % "lucene-core" % "3.3.0",
  "org.apache.lucene" % "lucene-highlighter" % "3.3.0",
  "se.scalablesolutions.akka" % "akka-actor" % "1.1.3",
  "se.scalablesolutions.akka" % "akka-remote" % "1.1.3",
  "org.slf4j" % "slf4j-api" % "1.6.1",
  "ch.qos.logback" % "logback-classic" % "0.9.29",
  "org.streum" % "configrity_2.9.0" % "0.7.0",
  "org.apache.yoko" % "yoko-spec-corba" % "1.3"
)

libraryDependencies ++= Seq(
  "org.specs2" %% "specs2" % "1.5" % "test",
  "org.specs2" %% "specs2-scalaz-core" % "6.0.RC2" % "test",
  "org.mockito" % "mockito-all" % "1.8.5" % "test",
  "se.scalablesolutions.akka" % "akka-testkit" % "1.1.3" % "test"
)

resolvers += "Sonatype OSS Snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/"

resolvers += "Akka Maven2 Repository" at "http://akka.io/repository/"

resolvers += "Scala Tools Snapshots" at "http://scala-tools.org/repo-snapshots/"

resolvers += "GuiceyFruit Release Repository" at "http://guiceyfruit.googlecode.com/svn/repo/releases/"

unmanagedBase <<= baseDirectory { base => base / "custom_lib" }

seq(scalateSettings:_*)

libraryDependencies += "com.mojolly.scalate" %% "scalate-generator" % "0.0.1" % "scalate"

scalateTemplateDirectory in Compile <<= (baseDirectory) {
  (basedir) => new File(basedir, "src/main/webapp/WEB-INF")
}
