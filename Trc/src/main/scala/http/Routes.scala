package http

import akka.actor.ActorRef
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.unmarshalling.{FromEntityUnmarshaller, PredefinedFromEntityUnmarshallers}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import akka.util.Timeout
import blockchain.{Block, BlockChain, GenesisBlock, Transaction}
import blockchain.BlockchainNetwork.{BlockchainQuery, TransactionBroadcast}
import blockchain.Mining.Mine
import de.heikoseeberger.akkahttpjson4s.Json4sSupport
import org.json4s.native.Serialization

import java.util.concurrent.TimeUnit
import scala.concurrent.{ExecutionContext, Future}
import org.json4s.{native, DefaultFormats, Formats}
import p2p.PeerToPeer.{AddPeer, GetPeers, Peers}

trait Routes extends Json4sSupport {
  val blockChainActor:           ActorRef
  implicit val executionContext: ExecutionContext

  implicit val serialization: Serialization.type = native.Serialization
  implicit val stringUnmarshallers: FromEntityUnmarshaller[String] =
    PredefinedFromEntityUnmarshallers.stringUnmarshaller

  implicit def json4sFormats: Formats = DefaultFormats
  implicit val timeout:       Timeout = Timeout(5, TimeUnit.SECONDS)

  val routes: Route =
    get {
      //curl http://localhost:9000/blocks
      path("blocks") {
        val chain: Future[Seq[Block]] = (blockChainActor ? BlockchainQuery).map { case blockChain: BlockChain =>
          List(GenesisBlock.copy()) ++ blockChain.allBlocks.slice(1, blockChain.allBlocks.length)
        }
        complete(chain)
      } ~
        // curl http://localhost:9001/peers
        path("peers") {
          complete((blockChainActor ? GetPeers).mapTo[Peers])
        }

    } ~
      post {
        //curl -X POST -d "Anton" http://localhost:9000/mineBlock
        path("mineBlock") {
          entity(as[String]) { data =>
            complete((blockChainActor ? Mine(data)).mapTo[Block].map { block: Block =>
              block
            })
          }
        } ~
          // curl -X POST -d "akka.tcp://BlockChain@node1:2552/user/blockChainActor" http://localhost:9001/addPeer
          path("addPeer") {
            entity(as[String]) { peerAddress =>
              blockChainActor ! AddPeer(peerAddress)
              complete(s"Added peer $peerAddress")
            }
          }
        //curl -X POST -d "{"sender":"Alice", "receiver":"Bob", "amount": "1000", "timestamp": ""10031}" http://localhost:9000/makeTransaction
        path("makeTransaction") {
          entity(as[Transaction]) { transaction =>
            blockChainActor ! TransactionBroadcast(transaction)
            complete(s"Transaction $transaction started")
          }
        }

      }
}
