package http

import akka.actor.ActorRef
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.unmarshalling.{FromEntityUnmarshaller, PredefinedFromEntityUnmarshallers}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import akka.util.Timeout
import blockchain.{Block, BlockChain, GenesisBlock}
import blockchain.BlockchainNetwork.BlockchainQuery
import blockchain.Mining.Mine
import de.heikoseeberger.akkahttpjson4s.Json4sSupport
import org.json4s.native.Serialization

import java.util.concurrent.TimeUnit
import scala.concurrent.{ExecutionContext, Future}
import org.json4s.{DefaultFormats, Formats, native}


trait Routes  extends Json4sSupport{
  val blockChainActor: ActorRef
  implicit val executionContext: ExecutionContext

  implicit val serialization: Serialization.type = native.Serialization
  implicit val stringUnmarshallers: FromEntityUnmarshaller[String] = PredefinedFromEntityUnmarshallers.stringUnmarshaller

  implicit def json4sFormats: Formats = DefaultFormats
  implicit val timeout: Timeout = Timeout(5, TimeUnit.SECONDS)

  val routes: Route =
    get {
      //curl http://localhost:9000/blocks
      path("blocks") {
        val chain: Future[Seq[Block]] = (blockChainActor ? BlockchainQuery).map {
          case blockChain: BlockChain => blockChain.blocks.slice(0, blockChain.blocks.length -1) :+ GenesisBlock.copy()
        }
        complete(chain)

      } ~
      //curl -X GET -d "Anton" http://localhost:9000/mineBlock
        path("mineBlock") {
          entity(as[String]) { data =>
            val block = blockChainActor ? Mine(data)
            complete(block)
          }
        }
    }
}

