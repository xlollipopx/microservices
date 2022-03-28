import actors.BlockChainActor
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import blockchain.BlockChain
import com.typesafe.config.ConfigFactory
import http.{Authentication, Routes}
import utils.FileProcessor

import scala.concurrent.ExecutionContextExecutor

object Application extends App with Routes {

  implicit val system:           ActorSystem              = ActorSystem("BlockChain")
  implicit val materializer:     ActorMaterializer        = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  val blockChainActor = system.actorOf(BlockChainActor.props(BlockChain()), "blockChainActor")
  val fileProcessor   = new FileProcessor()
  val Auth            = new Authentication(fileProcessor)
  fileProcessor.setSecretKey(Auth.generateSecretKey())

  val config = ConfigFactory.load("application.conf")

  Http().bindAndHandle(routes, config.getString("http.interface"), config.getInt("http.port"))
}
