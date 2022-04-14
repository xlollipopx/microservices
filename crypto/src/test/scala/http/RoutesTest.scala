//package http
//
//import akka.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCodes}
//import akka.http.scaladsl.testkit.ScalatestRouteTest
//import akka.http.scaladsl.server._
//import Directives._
//import akka.actor.ActorRef
//import akka.http.scaladsl.server.Directives._
//import akka.testkit.{TestActor, TestProbe}
//import blockchain.BlockChain.GenesisBlock
//import blockchain.{Block, BlockChain}
//import blockchain.BlockchainNetwork.BlockchainQuery
//import blockchain.Mining.Mine
//import org.scalatest.{FreeSpec, Matchers}
//import p2p.PeerToPeer.{AddPeer, GetPeers, Peers}
//import utils.CredentialFileProcessor
//
//import scala.concurrent.ExecutionContext
//
//class RoutesTest extends FreeSpec with Matchers with ScalatestRouteTest with Routes {
//
//  val peerToPeerProbe = TestProbe()
//  override val blockChainActor:           ActorRef                = peerToPeerProbe.ref
//  implicit override val executionContext: ExecutionContext        = ExecutionContext.global
//  val fileProcessor:                      CredentialFileProcessor = new CredentialFileProcessor()
//  val Auth:                               Authentication          = new Authentication(fileProcessor)
//
//  "A route get the blockchain from network" in {
//    peerToPeerProbe.setAutoPilot { (sender: ActorRef, msg: Any) =>
//      msg match {
//        case BlockchainQuery =>
//          sender ! BlockChain()
//          TestActor.NoAutoPilot
//      }
//    }
//
//    Get("/blocks") ~> routes ~> check {
//      responseAs[Seq[Block]] shouldEqual Seq(GenesisBlock)
//    }
//  }
//
//  "retrieve all peers" in {
//    peerToPeerProbe.setAutoPilot { (sender: ActorRef, msg: Any) =>
//      msg match {
//        case GetPeers =>
//          sender ! Peers(Seq("PeerOne"))
//          TestActor.NoAutoPilot
//      }
//    }
//    Get("/peers") ~> routes ~> check {
//      responseAs[Peers] shouldEqual Peers(Seq("PeerOne"))
//    }
//  }
//  "add a new peer" in {
//    Post("/addPeer", HttpEntity(ContentTypes.`text/html(UTF-8)`, "TestPeer")) ~> routes ~> check {
//      peerToPeerProbe.expectMsg(AddPeer("TestPeer"))
//    }
//  }
//
//  "add a new block" in {
//    peerToPeerProbe.setAutoPilot { (sender: ActorRef, msg: Any) =>
//      msg match {
//        case Mine(data) =>
//          sender ! "Mining started!"
//          TestActor.NoAutoPilot
//      }
//    }
//
//    Post("/mineBlock", HttpEntity(ContentTypes.`text/html(UTF-8)`, "MyBlock")) ~> routes ~> check {
//      val resp = responseAs[String]
//      assert(resp == "Mining started!")
//    }
//  }
//
//}
