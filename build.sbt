import sbt.Keys.scalacOptions
import sbt._

name := "cockpit"
organization := "ru.alesavin"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.12.7"
crossScalaVersions := Seq("2.12.7")

conflictManager := ConflictManager.strict

val customScalacOptions = Seq(
  "-unchecked",
  "-deprecation",
  "-explaintypes",
  "-feature",
  "-Xlint",
  "-Xfatal-warnings",
  "-language:postfixOps",
  "-language:higherKinds"
)

val customDependencies = Seq(
  "junit" % "junit" % "4.12" % Test,
  "org.scalatest" %% "scalatest" % "3.0.5" % Test
)

lazy val root = (project in file("."))
  .aggregate(core, zookeeper, akkaHttp)
  .settings(skip in publish := true)

lazy val core = (project in file("core"))
  .settings(
    name := "cockpit-core",
    scalacOptions ++= customScalacOptions,
    libraryDependencies ++= customDependencies
  )

lazy val zookeeper = (project in file("zookeeper"))
  .settings(
    name := "cockpit-zookeeper",
    scalacOptions ++= customScalacOptions,
    libraryDependencies ++= customDependencies ++ Seq(
      "org.apache.curator" % "curator-framework" % "4.2.0"
    )
  ).dependsOn(core % "compile->compile,test->test")

lazy val akkaHttp = (project in file("akka-http"))
  .settings(
    name := "cockpit-akka-http",
    scalacOptions ++= customScalacOptions,
    libraryDependencies ++= customDependencies
  )

//coverageEnabled := true

// TODO scalastyle