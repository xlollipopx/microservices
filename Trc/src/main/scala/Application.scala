import actors.BlockChainActor
import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, OneForOneStrategy, Props}
import akka.actor.Actor.emptyBehavior
import akka.actor.SupervisorStrategy.{Restart, Resume, Stop}
import akka.event.jul.Logger
import akka.http.scaladsl.Http
import akka.persistence.PersistentActor
import akka.stream.ActorMaterializer
import blockchain.BlockChain
import com.typesafe.config.ConfigFactory
import http.Routes
import routes.simpleRoute

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor}

object Application extends App with Routes{
  //  val localSystem = ActorSystem("LocalSystem", ConfigFactory.load("application.conf"))
  //  val localSimpleActor = localSystem.actorOf(Props[SimpleActor], "localSimpleActor")
  //  localSimpleActor ! "hiii"
  //  print("Hello!")
  implicit val system: ActorSystem = ActorSystem("BlockChain")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

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

