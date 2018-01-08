package com.contxt.kinesis

import com.amazonaws.services.kinesis.producer.{ KinesisProducer, KinesisProducerConfiguration, UserRecordResult }
import com.google.common.util.concurrent.ListenableFuture
import java.nio.ByteBuffer
import scala.concurrent._
import scala.language.implicitConversions
import scala.util.Try

/** A lightweight Scala wrapper around Kinesis Producer Library (KPL). */
trait ScalaKinesisProducer {

  /** Sends a record to a stream. See
    * [[[com.amazonaws.services.kinesis.producer.KinesisProducer.addUserRecord(String, String, String, ByteBuffer):ListenableFuture[UserRecordResult]*]]].
    */
  def send(partitionKey: String, data: ByteBuffer, explicitHashKey: Option[String] = None): Future[UserRecordResult]

  /** Flushes all the outgoing messages, returning a Future that completes when all the flushed messages have been sent.
    * See [[com.amazonaws.services.kinesis.producer.KinesisProducer.flushSync]].
    */
  def flushAll(): Future[Unit]

  /** Performs an orderly shutdown, waiting for all the outgoing messages before destroying the underlying producer. */
  def shutdown(): Future[Unit]
}

object ScalaKinesisProducer {
  def apply(streamName: String, producerConfig: KinesisProducerConfiguration): ScalaKinesisProducer = {
    val producer = new KinesisProducer(producerConfig)
    new ScalaKinesisProducerImpl(streamName, producer)
  }

  private[kinesis] implicit def listenableToScalaFuture[A](listenable: ListenableFuture[A]): Future[A] = {
    implicit val executionContext: ExecutionContextExecutor = scala.concurrent.ExecutionContext.global
    val promise = Promise[A]
    val callback = new Runnable {
      override def run(): Unit = promise.tryComplete(Try(listenable.get()))
    }
    listenable.addListener(callback, executionContext)
    promise.future
  }
}

private[kinesis] class ScalaKinesisProducerImpl(
  val streamName: String,
  private val producer: KinesisProducer
) extends ScalaKinesisProducer {
  import ScalaKinesisProducer.listenableToScalaFuture

  def send(partitionKey: String, data: ByteBuffer, explicitHashKey: Option[String]): Future[UserRecordResult] = {
    producer.addUserRecord(streamName, partitionKey, explicitHashKey.orNull, data)
  }

  def flushAll(): Future[Unit] = {
    import scala.concurrent.ExecutionContext.Implicits.global
    Future {
      blocking {
        producer.flushSync()
      }
    }
  }

  def shutdown(): Future[Unit] = {
    import scala.concurrent.ExecutionContext.Implicits.global
    val allFlushedFuture = flushAll()
    allFlushedFuture.onComplete(_ => producer.destroy())
    allFlushedFuture
  }
}
