organization in ThisBuild := "com.contxt"
scalaVersion in ThisBuild := "2.11.8"
version in ThisBuild := "1.0.2-SNAPSHOT"

val slf4j = "org.slf4j" % "slf4j-api" % "1.7.21"
val amazonKinesisProducer = "com.amazonaws" % "amazon-kinesis-producer" % "0.12.7"
val typesafeConfig = "com.typesafe" % "config" % "1.3.1"

libraryDependencies ++= Seq(
  slf4j,
  amazonKinesisProducer,
  typesafeConfig
)
