package com.contxt.kinesis

case class StreamId(
  /** AWS region name. */
  regionName: String,

    /** Stream name. */
  streamName: String
)
