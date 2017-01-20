name := """sample-petstore"""

version := "1.0"

scalaVersion := "2.11.8"

lazy val root = (project in file(".")).enablePlugins(PlayJava).enablePlugins(SbtWeb)

libraryDependencies ++= Seq(
  "io.swagger" %% "swagger-play2" % "1.5.3",
  "org.webjars" % "swagger-ui" % "2.2.8"
)