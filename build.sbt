import sbt.Keys.scalacOptions
import sbt._

name := "cockpit"
organization := "ru.alesavin"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.12.7"
crossScalaVersions := Seq("2.12.7")

conflictManager := ConflictManager.strict

val JunitVersion = "4.12"
val ScalaTestVersion = "3.0.5"
val CuratorVersion = "4.2.0"

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
  "junit" % "junit" % JunitVersion % Test,
  "org.scalatest" %% "scalatest" % ScalaTestVersion % Test
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
      "org.apache.curator" % "curator-framework" % CuratorVersion,
      "org.apache.curator" % "curator-test" % CuratorVersion % Test,
    )
  ).dependsOn(core % "compile->compile;test->test")

lazy val akkaHttp = (project in file("akka-http"))
  .settings(
    name := "cockpit-akka-http",
    scalacOptions ++= customScalacOptions,
    libraryDependencies ++= customDependencies
  )

// TODO coverageEnabled := true
// TODO scalastyle