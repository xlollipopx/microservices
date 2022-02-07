package producer

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.SerializationFeature
import domain.domain.Order
import org.apache.kafka.common.serialization.Serializer
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility
import org.codehaus.jackson.annotate.JsonMethod
import org.codehaus.jackson.map.ObjectMapper

import java.util

class OrderSerializer extends Serializer[Order] {
  override def configure(map: util.Map[String, _], b: Boolean): Unit = {}
  override def serialize(s: String, t: Order): Array[Byte] = {
    if (t == null)
      null
    else {
      val objectMapper = new ObjectMapper()
      objectMapper.setVisibility(JsonMethod.FIELD, Visibility.ANY);
      objectMapper.writeValueAsString(t).getBytes
    }
  }
  override def close(): Unit = {}
}
