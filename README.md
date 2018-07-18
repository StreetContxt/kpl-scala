# kpl-scala
A lightweight Scala wrapper around Kinesis Producer Library (KPL).

The main benefit of this library is working with Scala-native Futures when
interacting with KPL.


## Installation

```
resolvers in ThisBuild += Resolver.bintrayRepo("streetcontxt", "maven")
libraryDependencies += "com.streetcontxt" %% "kpl-scala" % "1.0.5"
```


## Usage

Here is a simple app that initializes the Kinesis producer and sends a string message.

```
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain
import com.amazonaws.services.kinesis.producer.KinesisProducerConfiguration
import com.contxt.kinesis.ScalaKinesisProducer
import java.nio.ByteBuffer
import scala.concurrent.Await
import scala.concurrent.duration._

object Main {
  def main(args: Array[String]): Unit = {
    val producerConfig = new KinesisProducerConfiguration()
      .setCredentialsProvider(new DefaultAWSCredentialsProviderChain)
      .setRegion("us-east-1")

    val producer = ScalaKinesisProducer("myStream", producerConfig)

    val sendFuture = producer.send(
      partitionKey = "myKey",
      data = ByteBuffer.wrap("myMessage".getBytes("UTF-8"))
    )
    Await.result(sendFuture, 10.seconds)
    Await.result(producer.shutdown(), Duration.Inf)
  }
}
```


## Amazon Licensing Restrictions
**KPL license is not compatible with open source licenses!** See
[this discussion](https://issues.apache.org/jira/browse/LEGAL-198) for more details.

As such, the licensing terms of this library is Apache 2 license **PLUS** whatever restrictions
are imposed by the KPL license.


## No Message Ordering
Kinesis producer library **does not provide message ordering guarantees** at a reasonable throughput,
see [this ticket](https://github.com/awslabs/amazon-kinesis-producer/issues/23) for more details.


## Integration Tests
This library is tested as part of [kcl-akka-stream](https://github.com/StreetContxt/kcl-akka-stream)
integration tests.
