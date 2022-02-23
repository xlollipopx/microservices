import actors.BlockChainActor
import akka.actor.ActorSystem
import akka.event.jul.Logger
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import blockchain.BlockChain
import com.typesafe.config.ConfigFactory
import http.Routes

import scala.concurrent.ExecutionContextExecutor


object Application extends App with Routes{

  implicit val system: ActorSystem = ActorSystem("BlockChain")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  val blockChainActor = system.actorOf(BlockChainActor.props(BlockChain()), "blockChainActor")

  val config = ConfigFactory.load("application.conf")
  val logger = Logger("WebServer")

  val seedHost = config.getString("blockchain.seedHost")

  if ( seedHost.nonEmpty ) {
    logger.info(s"Attempting to connect to seed-host ${seedHost}")
  } else {
    logger.info("No seed host configured, waiting for messages.")
  }

  Http().bindAndHandle(routes, config.getString("http.interface"), config.getInt("http.port"))
}

