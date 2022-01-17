import cats.Defer
import cats.effect.{ExitCode, IO, IOApp}
import cats.effect._
import conf.AppContext
import conf.app.AppConf
import org.http4s.server.blaze.BlazeServerBuilder
import io.circe.config.parser
import org.http4s.server.Server

import scala.concurrent.ExecutionContext
import scala.language.higherKinds

object Runner extends IOApp {

  override def run(args: List[String]): IO[ExitCode] = serverResource[IO]
    .use(_ => IO.never)
    .as(ExitCode.Success)

  private def serverResource[F[_]: ContextShift: ConcurrentEffect: Timer: Async: Defer]: Resource[F, Server[F]] =
    for {

      conf <- Resource.eval(parser.decodePathF[F, AppConf]("app"))

      httpApp <- AppContext.setUp[F](conf)
      server <- BlazeServerBuilder[F](ExecutionContext.global)
        .bindHttp(conf.server.port, conf.server.host)
        .withHttpApp(httpApp)
        .resource

    } yield server
}
