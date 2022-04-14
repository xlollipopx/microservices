import BlockChainConfig.{httpInterface, httpPort, redisPort, seedHost}
import actors.BlockChainActor
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import blockchain.BlockChain
import com.redis.RedisClient
import com.typesafe.config.{Config, ConfigFactory}
import http.{Authentication, Routes}
import p2p.PeerToPeer.AddPeer
import utils.{CredentialFileProcessor, CredentialMapProcessor, CredentialProcessor, CredentialRedisProcessor}

import scala.concurrent.ExecutionContextExecutor

object BlockChainConfig {
  val config: Config = ConfigFactory.load()

  val seedHost: String = config.getString("blockchain.seedHost")

  val httpInterface: String = config.getString("http.interface")
  val httpPort:      Int    = config.getInt("http.port")
  val redisPort:     Int    = config.getInt("redis.port")

}

object Application extends App with Routes {

  implicit val system:           ActorSystem              = ActorSystem("BlockChain")
  implicit val materializer:     ActorMaterializer        = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  val blockChainActor = system.actorOf(BlockChainActor.props(BlockChain()), "blockChainActor")

  if (seedHost.nonEmpty) {
    blockChainActor ! AddPeer(seedHost)
  }

  println("PoRT:" + redisPort)
  val client = new RedisClient("localhost", redisPort)
  val fileProcessor: CredentialProcessor = new CredentialRedisProcessor(client)
  //val fileProcessor: CredentialProcessor = new CredentialMapProcessor()
  fileProcessor.setSecretKey(Authentication.generateSecretKey())
  val key = fileProcessor.getSecretKey

  val Auth = new Authentication(fileProcessor, key)

  val config = ConfigFactory.load("application.conf")

  Http().bindAndHandle(routes, httpInterface, httpPort)
}
