package blockchain

import actors.BlockChainActor
import akka.actor.{ActorRef, ActorSystem}
import akka.testkit.{ImplicitSender, TestKit, TestProbe}
import blockchain.Mining.Mine
import org.scalatest.{BeforeAndAfterAll, FlatSpecLike, FreeSpec, GivenWhenThen, Matchers}

class MiningTest
  extends TestKit(ActorSystem("BlockChain"))
    with FlatSpecLike
    with ImplicitSender
    with GivenWhenThen
    with BeforeAndAfterAll
    with Matchers {

  trait TestActor {
    val blockChain = BlockChain()
    val peerToPeer = TestProbe()
    val blockChainActor: ActorRef = system.actorOf(BlockChainActor.props(blockChain))
  }

  override def afterAll: Unit = {
    TestKit.shutdownActorSystem(system)
  }

  it should "send mine request" in new TestActor {
    blockChainActor ! Mine("Data")
    expectMsg("Mining started!")
  }

  it should "add new block to a blockchain" in new TestActor {
    blockChainActor ! Mine("Data")
    Thread.sleep(10000)
    assert(blockChain.allBlocks.length == 2)
  }

  it should "ignore second mine request" in new TestActor {
    blockChainActor ! Mine("Data")
    blockChainActor ! Mine("Data")
    expectMsg("Mining started!")
    expectMsg("Mining has already started!")
  }

}
