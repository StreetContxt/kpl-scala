package com.contxt.kinesis

import com.amazonaws.services.kinesis.producer.{ KinesisProducer, KinesisProducerConfiguration, UserRecordResult }
import com.google.common.util.concurrent.ListenableFuture
import com.typesafe.config.{ Config, ConfigFactory }
import java.nio.ByteBuffer
import scala.concurrent._
import scala.language.implicitConversions
import scala.util.Try
import scala.collection.JavaConversions._
import scala.concurrent.ExecutionContext.Implicits.global

/** A lightweight Scala wrapper around Kinesis Producer Library (KPL). */
trait ScalaKinesisProducer {

  def streamId: StreamId

  /** Sends a record to a stream. See
    * [[[com.amazonaws.services.kinesis.producer.KinesisProducer.addUserRecord(String, String, String, ByteBuffer):ListenableFuture[UserRecordResult]*]]].
    */
  def send(partitionKey: String, data: ByteBuffer, explicitHashKey: Option[String] = None): Future[UserRecordResult]

  /** Performs an orderly shutdown, waiting for all the outgoing messages before destroying the underlying producer. */
  def shutdown(): Future[Unit]
}

object ScalaKinesisProducer {
  def apply(
    streamName: String,
    kplConfig: KinesisProducerConfiguration,
    config: Config = ConfigFactory.load()
  ): ScalaKinesisProducer = {
    val producerStats = ProducerStats.getInstance(config)
    ScalaKinesisProducer(streamName, kplConfig, producerStats)
  }

  def apply(
    streamName: String,
    kplConfig: KinesisProducerConfiguration,
    producerStats: ProducerStats
  ): ScalaKinesisProducer = {
    val streamId = StreamId(kplConfig.getRegion, streamName)
    val producer = new KinesisProducer(kplConfig)
    new ScalaKinesisProducerImpl(streamId, producer, producerStats)
  }

  private[kinesis] implicit def listenableToScalaFuture[A](listenable: ListenableFuture[A]): Future[A] = {
    val promise = Promise[A]
    val callback = new Runnable {
      override def run(): Unit = promise.tryComplete(Try(listenable.get()))
    }
    listenable.addListener(callback, ExecutionContext.global)
    promise.future
  }
}

private[kinesis] class ScalaKinesisProducerImpl(
  val streamId: StreamId,
  private val producer: KinesisProducer,
  private val stats: ProducerStats
) extends ScalaKinesisProducer {
  import ScalaKinesisProducer.listenableToScalaFuture

  stats.reportInitialization(streamId)

  def send(partitionKey: String, data: ByteBuffer, explicitHashKey: Option[String]): Future[UserRecordResult] = {
    stats.trackSend(streamId, data.remaining) {
      producer.addUserRecord(streamId.streamName, partitionKey, explicitHashKey.orNull, data).map { result =>
        if (!result.isSuccessful) throwSendFailedException(result) else result
      }
    }
  }

  def shutdown(): Future[Unit] = {
    val allFlushedFuture = flushAll()
    allFlushedFuture.onComplete { _ =>
      producer.destroy()
      stats.reportShutdown(streamId)
    }
    allFlushedFuture
  }

  private def throwSendFailedException(result: UserRecordResult): Nothing = {
    val attemptCount = result.getAttempts.size
    val errorMessage = result.getAttempts.lastOption.map(_.getErrorMessage)
    throw new RuntimeException(
      s"Sending a record to $streamId failed after $attemptCount attempts, last error message: $errorMessage."
    )
  }

  private def flushAll(): Future[Unit] = {
    Future {
      blocking {
        producer.flushSync()
      }
    }
  }
}
