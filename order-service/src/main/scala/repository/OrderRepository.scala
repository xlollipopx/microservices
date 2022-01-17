package repository

import java.util.UUID
import cats.Functor
import cats.effect.Bracket
import doobie.{ConnectionIO, Fragment, Transactor}
import doobie.implicits._
import cats.implicits._
import doobie.postgres.implicits._
import cats.effect._
import domain.domain.Order

import scala.language.higherKinds

trait OrderRepository[F[_]] {
  def create(order: Order): F[Int]
  def all(): F[List[Order]]
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
  override def create(order: Order): F[Int] =
    (createOrder ++
      fr"VALUES(${UUID.randomUUID()}, ${order.cost})").update.run.transact(tx)

  override def all(): F[List[Order]] =
    selectOrders.query[Order].to[List].transact(tx)
}
