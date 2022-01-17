package domain
import enumeratum.{CirceEnum, Enum, EnumEntry}

import scala.collection.immutable

object domain {
  final case class Order(cost: Int)
  final case class OrderWithStatus(cost: Int, status: OrderStatus)

  sealed trait OrderStatus extends EnumEntry
  object OrderStatus extends Enum[OrderStatus] with CirceEnum[OrderStatus] {

    val values: immutable.IndexedSeq[OrderStatus] = findValues
    final case object NotComplete extends OrderStatus
    final case object Ordered extends OrderStatus
    final case object Assigned extends OrderStatus
    final case object Delivered extends OrderStatus
  }
}
