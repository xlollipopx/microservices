package conf

import cats.effect.{Concurrent, ContextShift, Resource, Sync, Timer}
import conf.app.AppConf
import conf.db.{migrator, transactor}

object AppContext {

  def setUp[F[_]: ContextShift: Sync: Concurrent: Timer](conf: AppConf): Resource[F, Unit] = for {

    tx       <- transactor[F](conf.db)
    migrator <- Resource.eval(migrator[F](conf.db))
    _        <- Resource.eval(migrator.migrate())

  } yield ()

}
