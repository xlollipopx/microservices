package http

import org.http4s.server.Router
import cats.Monad
import cats.effect.Sync
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import cats.implicits._
import io.circe.generic.auto._
import org.http4s.circe.CirceEntityCodec.{circeEntityDecoder, circeEntityEncoder}
import org.http4s.implicits.http4sKleisliResponseSyntaxOptionT

import scala.language.higherKinds

final case class Order(cost: Int)

final case class Routes[F[_]: Monad: Sync]() extends Http4sDsl[F] {
  private val prefixPath = ""

  private val httpRoutes: HttpRoutes[F] = HttpRoutes.of[F] {
    case GET -> Root / "welcome" =>
      for {
        res <- Ok("Hi")
      } yield res

    case req @ POST -> Root / "make-order" =>
      req.as[Order].flatMap { dto =>
        for {
          res <- Ok(dto)
        } yield res
      }
  }

  val routes: HttpRoutes[F] = Router(
    prefixPath -> httpRoutes
  )
}
