organization in ThisBuild := "com.contxt"
scalaVersion in ThisBuild := "2.11.8"

val slf4j = "org.slf4j" % "slf4j-api" % "1.7.21"
val amazonKinesisProducer = "com.amazonaws" % "amazon-kinesis-producer" % "0.12.8"

libraryDependencies ++= Seq(
  slf4j,
  amazonKinesisProducer
)
