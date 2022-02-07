package service

import cats.Monad
import cats.effect.Sync
import domain.domain.{Order, OrderStatus}
import repository.OrderRepository
import cats.implicits._
import producer.OrderProducer
import producer.OrderProducer.sendOrderForValidation

import scala.language.higherKinds

trait OrderService[F[_]] {
  def all(): F[List[Order]]
  def create(order:        Order):       F[Int]
  def changeStatus(status: OrderStatus): F[Int]
}

object OrderService {
  def of[F[_]: Sync](
    productItemRepository: OrderRepository[F]
  ): OrderServiceImpl[F] =
    new OrderServiceImpl[F](productItemRepository)
}

class OrderServiceImpl[F[_]: Sync: Monad](orderRepository: OrderRepository[F]) extends OrderService[F] {
  override def all(): F[List[Order]] = orderRepository.all()
  override def create(order: Order): F[Int] = for {
    _ <- orderRepository.create(order)
    _ <- sendOrderForValidation(OrderProducer.topic, order)
  } yield ()

  override def changeStatus(status: OrderStatus): F[Int] = ???
}
