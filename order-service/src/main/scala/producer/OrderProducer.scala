package producer

import cats.effect.IO
import cats.effect._
import domain.domain.Order
import java.util
import org.codehaus.jackson.map.ObjectMapper

import org.apache.kafka.clients.producer.{Callback, KafkaProducer, ProducerRecord, RecordMetadata}

import java.util.Properties
import scala.language.higherKinds
import org.apache.kafka.common.serialization.Serializer

object OrderProducer {
  val props: Properties = new Properties()
  props.put("bootstrap.servers", "localhost:9092")
  props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  props.put("value.serializer", "producer.OrderSerializer")
  props.put("acks", "all")
  val producer = new KafkaProducer[String, Order](props)
  val topic    = "order-validation"

  def sendOrderForValidation[F[_]](topic: String, value: Order) = {
    IO.pure {
      val record   = new ProducerRecord[String, Order](topic, value)
      val metadata = producer.send(record)
      metadata.get().offset()
    }
  }

  def main(args: Array[String]): Unit = {
    sendOrderForValidation(topic, Order(20)).unsafeRunSync()
  }
}
