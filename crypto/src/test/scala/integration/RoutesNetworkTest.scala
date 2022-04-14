package integration

import actors.BlockChainActor
import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.model.headers.{Accept, RawHeader}
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, MediaTypes}
import akka.http.scaladsl.testkit.{RouteTestTimeout, ScalatestRouteTest}
import akka.pattern.ask
import akka.testkit.{ImplicitSender, TestKit, TestProbe}
import blockchain.{BlockChain, Transaction}
import blockchain.BlockChain.GenesisBlock
import de.heikoseeberger.akkahttpjson4s.Json4sSupport
import http.{Authentication, JsonHelper, Routes}
import model.login.{AuthHeader, Blocks, Peer, PeerRequest, Request}
import org.json4s.native.Serialization
import org.json4s.{DefaultFormats, FieldSerializer, Formats}
import org.scalatest.{BeforeAndAfterAll, FlatSpecLike, FreeSpec, Matchers}
import p2p.PeerToPeer.{AddPeer, GetPeers, Peers}
import utils.CredentialFileProcessor

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.DurationInt

class RoutesNetworkTest extends FlatSpecLike with Matchers with ScalatestRouteTest with BeforeAndAfterAll {

  trait TestRoutes extends Routes {
    val blockChain:                BlockChain              = BlockChain()
    override val blockChainActor:  ActorRef                = system.actorOf(BlockChainActor.props(blockChain))
    override val executionContext: ExecutionContext        = ExecutionContext.global
    val fileProcessor:             CredentialFileProcessor = new CredentialFileProcessor()
    fileProcessor.setSecretKey(Authentication.generateSecretKey())
    val key = fileProcessor.getSecretKey
    val Auth: Authentication = new Authentication(fileProcessor, key)
  }

  implicit def default(implicit system: ActorSystem): RouteTestTimeout = RouteTestTimeout(5.seconds)

  it should "add a new peer" in new TestRoutes {
    val peerReq:  PeerRequest = PeerRequest(Peer("some/peer"), AuthHeader(""))
    val sk:       String      = fileProcessor.getSecretKey
    val jsonPeer: String      = write(peerReq)
    val token:    String      = Auth.createToken("localhost", 1, sk)

    Post("/addPeer", HttpEntity(ContentTypes.`text/html(UTF-8)`, "TestPeer"))
      .withHeaders(Accept(MediaTypes.`application/json`))
      .withEntity(jsonPeer) ~> RawHeader("Authorization", token) ~> routes ~> check {

      assert(responseAs[String].contains(s"Added peer ${peerReq.body.peer}"))

    }
  }

  it should "get correct balance" in new TestRoutes {
    val sk:    String = fileProcessor.getSecretKey
    val token: String = Auth.createToken("localhost", 1, sk)

    Post("/balance") ~> RawHeader("Authorization", token) ~> routes ~> check {
      assert(responseAs[String].contains("0"))
    }
  }

  it should "give reward to the node that minded a block" in new TestRoutes {
    val sk:    String = fileProcessor.getSecretKey
    val token: String = Auth.createToken("localhost", 1, sk)

    Post("/mineBlock")
      .withHeaders(Accept(MediaTypes.`application/json`)) ~> RawHeader("Authorization", token) ~> routes ~> check {
      assert(responseAs[String].contains(s"Mining started!"))
    }
    Thread.sleep(5000)
    Post("/balance") ~> RawHeader("Authorization", token) ~> routes ~> check {
      assert(responseAs[String].contains("100"))
    }
  }

  it should "add pending transaction in newly mined block" in new TestRoutes {
    val sk:    String = fileProcessor.getSecretKey
    val token: String = Auth.createToken("localhost", 1, sk)

    Post("/mineBlock")
      .withHeaders(Accept(MediaTypes.`application/json`)) ~> RawHeader("Authorization", token) ~> routes ~> check {
      assert(responseAs[String].contains(s"Mining started!"))
    }
    Thread.sleep(5000)

    val transaction: Transaction = Transaction("localhost", "Bob", 10, 1920)
    val req:         String      = write(Request(transaction, AuthHeader("")))

    Post("/makeTransaction")
      .withHeaders(Accept(MediaTypes.`application/json`))
      .withEntity(req) ~> RawHeader("Authorization", token) ~> routes ~> check {
      assert(responseAs[String].contains(s"Transaction ${transaction} started"))
    }

    Post("/mineBlock")
      .withHeaders(Accept(MediaTypes.`application/json`)) ~> RawHeader("Authorization", token) ~> routes ~> check {
      assert(responseAs[String].contains(s"Mining started!"))
    }
    Thread.sleep(5000)

    Get("/blocks")
      .withHeaders(Accept(MediaTypes.`application/json`)) ~> RawHeader("Authorization", token) ~> routes ~> check {
      parse(responseAs[String]).extract[Blocks].blocks.last.transactions.length shouldEqual 2
    }
  }

  it should "not add pending transaction in newly mined block when not enough money" in new TestRoutes {
    val sk:    String = fileProcessor.getSecretKey
    val token: String = Auth.createToken("localhost", 1, sk)
    val transaction = Transaction("localhost", "Bob", 10, 1920)
    val req         = write(Request(transaction, AuthHeader("")))

    Post("/makeTransaction")
      .withHeaders(Accept(MediaTypes.`application/json`))
      .withEntity(req) ~> RawHeader("Authorization", token) ~> routes ~> check {
      assert(responseAs[String].contains(s"Transaction ${transaction} started"))

    }
    Post("/mineBlock")
      .withHeaders(Accept(MediaTypes.`application/json`)) ~> RawHeader("Authorization", token) ~> routes ~> check {
      assert(responseAs[String].contains(s"Mining started!"))
    }
    Thread.sleep(5000)

    Get("/blocks")
      .withHeaders(Accept(MediaTypes.`application/json`)) ~> RawHeader("Authorization", token) ~> routes ~> check {
      parse(responseAs[String]).extract[Blocks].blocks.last.transactions.length shouldEqual 1
    }
  }

}
