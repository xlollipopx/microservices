package conf

import cats.effect.{Concurrent, ContextShift, Resource, Sync, Timer}
import conf.app.AppConf
import conf.db.{migrator, transactor}
import http.{HttpApi, Routes}
import org.http4s.HttpApp
import org.http4s.implicits.http4sKleisliResponseSyntaxOptionT

import scala.language.higherKinds

object AppContext {

  def setUp[F[_]: ContextShift: Sync: Concurrent: Timer](conf: AppConf): Resource[F, HttpApp[F]] = for {

    tx       <- transactor[F](conf.db)
    migrator <- Resource.eval(migrator[F](conf.db))
    _        <- Resource.eval(migrator.migrate())

    httpApp = Routes[F]().routes.orNotFound
  } yield httpApp

}
