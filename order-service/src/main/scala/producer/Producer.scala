package producer

import cats.effect.IO
import cats.effect._
import org.apache.kafka.clients.producer.{Callback, KafkaProducer, ProducerRecord, RecordMetadata}

import java.util.Properties
import scala.language.higherKinds

object Producer {
  val props: Properties = new Properties()
  props.put("bootstrap.servers", "localhost:9092")
  props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  props.put("acks", "all")
  val producer = new KafkaProducer[String, String](props)
  val topic    = "order-validation"

  def sendToKafka[F[_]](topic: String, value: String) = {
    IO.pure {
      val record   = new ProducerRecord[String, String](topic, value)
      val metadata = producer.send(record)
      metadata.get().offset()
    }
  }

  def main(args: Array[String]): Unit = {}
}
