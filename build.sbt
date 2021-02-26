name := "recipes-api"
organization := "com.example"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava, SwaggerPlugin)

scalaVersion := "2.13.1"

libraryDependencies += guice

enablePlugins(PlayEbean)
swaggerDomainNameSpaces := Seq("models", "controllers.dto")

libraryDependencies += evolutions
libraryDependencies += jdbc

libraryDependencies += "org.webjars" % "swagger-ui" % "3.35.0"

libraryDependencies += "com.h2database" % "h2" % "1.4.200"
libraryDependencies += "org.postgresql" % "postgresql" % "42.2.16"
libraryDependencies += "org.glassfish.jaxb" % "jaxb-core" % "2.3.0.1"
libraryDependencies += "org.glassfish.jaxb" % "jaxb-runtime" % "2.3.2"
libraryDependencies ++= Seq(ehcache)
