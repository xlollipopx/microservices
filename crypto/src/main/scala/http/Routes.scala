package http

import blockchain.BlockChain.GenesisBlock
import blockchain.{Block, BlockChain, Transaction}
import blockchain.BlockchainNetwork.{Balance, BlockchainQuery, TransactionBroadcast}
import blockchain.Mining.Mine
import akka.actor.ActorRef
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.unmarshalling.{FromEntityUnmarshaller, PredefinedFromEntityUnmarshallers}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.`Access-Control-Expose-Headers`
import akka.http.scaladsl.server.{RejectionHandler, Route}
import akka.pattern.ask
import akka.util.Timeout
import ch.megard.akka.http.cors.scaladsl.CorsDirectives
import ch.megard.akka.http.cors.scaladsl.CorsDirectives.cors
import ch.megard.akka.http.cors.scaladsl.settings.CorsSettings
import de.heikoseeberger.akkahttpjson4s.Json4sSupport

import java.util.concurrent.TimeUnit
import scala.concurrent.{ExecutionContext, Future}
import p2p.PeerToPeer.{AddPeer, GetPeers, Peers}
import http.JsonHelper
import model.login.{Blocks, Credentials, LoginRequest, PeerRequest, Request}
import utils.FileProcessor

trait Routes extends Json4sSupport with JsonHelper {

  val blockChainActor:           ActorRef
  implicit val executionContext: ExecutionContext
  val fileProcessor:             FileProcessor
  val Auth:                      Authentication

  implicit val stringUnmarshallers: FromEntityUnmarshaller[String] =
    PredefinedFromEntityUnmarshallers.stringUnmarshaller

  implicit val timeout: Timeout = Timeout(5, TimeUnit.SECONDS)
  val settings = CorsSettings.defaultSettings
    .withAllowCredentials(true)
    .withExposedHeaders(scala.collection.immutable.Seq("Access-token"))

  val unauthRoutes: Route = post {
    //curl -v -H POST http://localhost:9000/signup -H 'Content-Type: application/json' -d '{"username":"admin", "password":"admin" }'
    path("signup") {
      entity(as[String]) { data =>
        val req = parse(data).extract[Credentials]

        fileProcessor.getCredentials match {
          case Some(_) => complete(StatusCodes.Unauthorized -> "You have already signed up!")
          case None =>
            val secretKey = fileProcessor.getSecretKey
            fileProcessor.setCredentials(req)
            respondWithToken(req.username, secretKey)

        }
      }

    } ~
      //curl -v -H POST http://localhost:9000/signin -H 'Content-Type: application/json' -d '{"username":"http://localhost:9000", "password":"admin" }'
      path("signin") {
        entity(as[String]) { data =>
          val req = parse(data).extract[LoginRequest]
          req match {
            case _ if Auth.checkPassword(req.username, req.password) =>
              val secretKey = fileProcessor.getSecretKey
              respondWithToken(req.username, secretKey)
            case _ => complete(StatusCodes.Unauthorized)
          }
        }
      }
  }

  val authRoutes: Route =
    get {

      //curl -H "Authorization: " http://localhost:9000/blocks
      path("blocks") {
        Auth.authenticated { _ =>
          val chain: Future[Blocks] = (blockChainActor ? BlockchainQuery).map { case blockChain: BlockChain =>
            Blocks(List(GenesisBlock.copy()) ++ blockChain.allBlocks.slice(1, blockChain.allBlocks.length))
          }
          complete(chain)
        }
      } ~
        //curl -H "Authorization: " http://localhost:9001/peers
        path("peers") {
          Auth.authenticated { _ =>
            complete((blockChainActor ? GetPeers).mapTo[Peers])
          }
        }

    } ~
      post {
        //curl -H "Authorization: " -X POST http://localhost:9000/makeTransaction -H 'Content-Type: application/json' -d '{"sender":"Anton", "receiver":"Bob", "amount": 10, "timestamp": 10031 }'
        path("makeTransaction") {
          Auth.authenticated { user =>
            entity(as[String]) { data =>
              complete {

                val req = parse(data).extract[Request]
                blockChainActor ! TransactionBroadcast(req.body)
                s"Transaction ${req.body} started"
              }
            }
          }
        } ~
          // curl -H "Authorization: " -X POST -d "http://localhost:9000" http://localhost:9000/balance
          path("balance") {
            Auth.authenticated { user =>
              entity(as[String]) { _ =>
                complete((blockChainActor ? Balance(user)).mapTo[String].map { value: String =>
                  value
                })
              }
            }
          }
      } ~ post {
        //curl -H "Authorization: " -X POST -d "Anton" http://localhost:9000/mineBlock
        path("mineBlock") {
          Auth.authenticated { user =>
            entity(as[String]) { data =>
              complete((blockChainActor ? Mine(user)).mapTo[String].map { msg: String =>
                msg
              })
            }
          }
        } ~
          // curl -H "Authorization: " -X POST 'Content-Type: application/json' -d '{"peer": "akka.tcp://BlockChain@node1:2552/user/blockChainActor"}' http://localhost:9000/addPeer
          path("addPeer") {
            Auth.authenticated { _ =>
              entity(as[String]) { data =>
                println(data)
                val req = parse(data).extract[PeerRequest]
                blockChainActor ! AddPeer(req.body.peer)
                complete(s"Added peer ${req.body.peer}")
              }
            }
          }
      }

  def respondWithToken(username: String, sk: String): Route = {
    val token = Auth.createToken(username, 1, sk)
    respondWithHeaders(
      headers.RawHeader("Access-token", token),
      new `Access-Control-Expose-Headers`(
        scala.collection.immutable.Seq("Access-token")
      )
    ) {
      complete(StatusCodes.OK)
    }
  }

  val routes: Route = handleRejections(CorsDirectives.corsRejectionHandler) {
    cors(settings) {
      handleRejections(RejectionHandler.default) { unauthRoutes ~ authRoutes }
    }
  }

}
