inThisBuild(
  List(
    organization := "io.github.streetcontxt",
    homepage := Some(url("https://github.com/streetcontxt/kcl-akka-stream")),
    licenses := List("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")),
    developers := List(
      Developer(
        "agenovese",
        "Angelo Gerard Genovese",
        "angelo.gerard.genovese@gmail.com",
        url("https://github.com/agenovese")
      )
    )
  )
)

organization in ThisBuild := "io.github.streetcontxt"
scalaVersion in ThisBuild := "2.13.1"
crossScalaVersions := Seq("2.11.12", "2.12.11", "2.13.1")
licenses in ThisBuild += ("Apache-2.0", url("https://www.apache.org/licenses/LICENSE-2.0.html"))

name := "kpl-scala"

val slf4j = "org.slf4j" % "slf4j-api" % "1.7.25"
val amazonKinesisProducer = "com.amazonaws" % "amazon-kinesis-producer" % "0.12.11"
val typesafeConfig = "com.typesafe" % "config" % "1.3.1"

libraryDependencies ++= Seq(
  slf4j,
  amazonKinesisProducer,
  typesafeConfig
)
