package p2p

import actors.BlockChainActor
import akka.actor.{ActorRef, ActorSystem}
import akka.pattern.ask
import akka.testkit.{ImplicitSender, TestKit, TestProbe}
import akka.util.Timeout
import blockchain.BlockChain
import org.scalatest.{BeforeAndAfterAll, FlatSpecLike, GivenWhenThen, Matchers}
import p2p.PeerToPeer.{AddPeer, GetPeers, HandShake, Peers}

import java.util.concurrent.TimeUnit
import scala.concurrent.ExecutionContext.Implicits.global

class Peer2PeerTest
  extends TestKit(ActorSystem("BlockChain"))
    with FlatSpecLike
    with ImplicitSender
    with GivenWhenThen
    with BeforeAndAfterAll
    with Matchers {

  override def afterAll: Unit = {
    TestKit.shutdownActorSystem(system)
  }

  trait TestActor {
    implicit val timeout: Timeout = Timeout(5, TimeUnit.SECONDS)

    val blockChain:      BlockChain = BlockChain()
    val peerToPeer:      TestProbe  = TestProbe()
    val blockChainActor: ActorRef   = system.actorOf(BlockChainActor.props(blockChain))
  }

  it should "not add self reference to peers" in new TestActor {
    blockChainActor ! AddPeer(blockChainActor.toString())

    (blockChainActor ? GetPeers).map { case Peers(seq) =>
      assert(seq.isEmpty)
    }
  }

  it should "add reference of another node peers" in new TestActor {
    blockChainActor ! AddPeer("localhost:1234")

    (blockChainActor ? GetPeers).map { case Peers(seq) =>
      assert(seq.size == 1)
    }
  }

  it should "add us as a peer when we send a handshake" in new TestActor {
    val probe = TestProbe().ref
    probe ! HandShake

    (probe ? GetPeers).map { case Peers(seq) =>
      assert(seq.size == 1)
    }
  }

}
