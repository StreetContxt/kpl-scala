# kpl-scala
A lightweight Scala wrapper around Kinesis Producer Library (KPL).

The main benefit of this library is working with Scala-native Futures when
interacting with KPL.


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
