package com.contxt.kinesis

import com.amazonaws.services.kinesis.producer.UserRecordResult
import com.typesafe.config.{ Config, ConfigFactory }
import org.slf4j.LoggerFactory
import scala.concurrent.Future
import scala.util.control.NonFatal

trait ProducerStats {
  def trackSend(streamId: StreamId, size: Int)(closure: => Future[UserRecordResult]): Future[UserRecordResult]
  def reportInitialization(streamId: StreamId): Unit
  def reportShutdown(streamId: StreamId): Unit
}

object ProducerStats {
  private val log = LoggerFactory.getLogger(classOf[ProducerStats])

  def getInstance(config: Config): ProducerStats = {
    try {
      val className = config.getString("com.contxt.kinesis.producer.stats-class-name")
      Class.forName(className).newInstance().asInstanceOf[ProducerStats]
    }
    catch {
      case NonFatal(e) =>
        log.error("Could not load a `ProducerStats` instance, falling back to `NoopProducerStats`.", e)
        new NoopProducerStats
    }
  }
}

class NoopProducerStats extends ProducerStats {
  def trackSend(streamId: StreamId, size: Int)(closure: => Future[UserRecordResult]): Future[UserRecordResult] = closure
  def reportInitialization(streamId: StreamId): Unit = {}
  def reportShutdown(streamId: StreamId): Unit = {}
}
