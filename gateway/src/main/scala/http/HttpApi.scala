package http

import cats.effect.Async
import org.http4s.HttpRoutes

import scala.language.higherKinds

object HttpApi {
  def make[F[_]: Async](): HttpApi[F] =
    new HttpApi[F]() {}
}

sealed abstract class HttpApi[F[_]: Async](
) {

  val routes: HttpRoutes[F] = Routes[F]().routes

}
