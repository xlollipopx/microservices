package repository

import java.util.UUID
import cats.Functor
import cats.effect.Bracket
import doobie.{ConnectionIO, Fragment, Transactor}
import doobie.implicits._
import cats.implicits._
import doobie.postgres.implicits._
import cats.effect._
import domain.domain.{Order, OrderStatus, OrderWithStatus}

import scala.language.higherKinds

trait OrderRepository[F[_]] {
  def create(order: OrderWithStatus): F[Int]
  def all(): F[List[Order]]
  def changeStatus(status: OrderStatus): F[Int]
}

object OrderRepository {
  def of[F[_]: Sync](tx: Transactor[F]): DoobieOrderRepository[F] =
    new DoobieOrderRepository[F](tx)
}

class DoobieOrderRepository[F[_]: Functor: Sync: Bracket[*[_], Throwable]](
  tx: Transactor[F]
) extends OrderRepository[F] {

  val createOrder  = fr"INSERT INTO orders"
  val selectOrders = fr"SELECT * FROM orders"
  val updateOrders = fr"UPDATE orders"

  override def create(order: OrderWithStatus): F[Int] =
    (
      createOrder ++
        fr"VALUES(${order.id}, ${order.cost})"
    ).update.run.transact(tx)

  override def all(): F[List[Order]] =
    selectOrders.query[Order].to[List].transact(tx)

  override def changeStatus(status: OrderStatus): F[Int] =
    (updateOrders ++ fr"SET name = ${supplier.name} WHERE uuid = ${supplier.id}").update.run.transact(tx)
}
