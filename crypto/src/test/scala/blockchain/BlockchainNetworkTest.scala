package blockchain
import actors.BlockChainActor
import akka.actor.{ActorRef, ActorSystem}
import akka.pattern.ask
import akka.testkit.{ImplicitSender, TestKit, TestProbe}
import akka.util.Timeout
import blockchain.BlockChain.GenesisBlock
import blockchain.BlockchainNetwork.{AddBlockRequest, Balance, BlockchainQuery, BlocksIncome}
import blockchain.Mining.Mine
import org.scalatest.{BeforeAndAfterAll, FlatSpecLike, GivenWhenThen, Matchers}
import p2p.PeerToPeer.AddPeer

import java.util.concurrent.TimeUnit
import scala.concurrent.ExecutionContext.Implicits.global

class BlockchainNetworkTest
  extends TestKit(ActorSystem("BlockChain"))
    with FlatSpecLike
    with ImplicitSender
    with GivenWhenThen
    with BeforeAndAfterAll
    with Matchers {

  trait TestActor {
    implicit val timeout: Timeout    = Timeout(5, TimeUnit.SECONDS)
    val blockChain:       BlockChain = BlockChain()
    val blockChainActor:  ActorRef   = system.actorOf(BlockChainActor.props(blockChain))

    val blockChainTwo:      BlockChain = BlockChain()
    val blockChainActorTwo: ActorRef   = system.actorOf(BlockChainActor.props(blockChainTwo))

  }

  override def afterAll: Unit = {
    TestKit.shutdownActorSystem(system)
  }

  it should "replace old blockchain when larger blockchain received" in new TestActor {
    val newChain = BlockChain().addBlock("Data")
    blockChainActor ! BlocksIncome(newChain.allBlocks)
    expectMsg("Chain updated, new size: 2")
  }

  it should "return correct balance" in new TestActor {
    blockChainActor ! Mine("Alice")
    Thread.sleep(5000)
    blockChainActor ! Transaction("Alice", "Bob", 10, 1920)

    blockChainActor ! Mine("Bob")
    Thread.sleep(5000)
    (blockChainActor ? Balance("Bob")).mapTo[String].map(balance => assert(balance == "110"))
  }

  it should "not add a block fraudly mined by another node" in new TestActor {
    blockChainActor ! AddBlockRequest(Block(1, GenesisBlock.hash, 10, List(), 1313, "LSImkmd", "lsdlnsjdn"))
    (blockChainActor ? BlockchainQuery).map { case chain: BlockChain =>
      assert(chain.getBlockchainSize == 1)
    }
  }

  it should "add a block mined by another node in new TestActor" in new TestActor {
    val newChain = BlockChain().addBlock("Data")
    blockChainActor ! newChain.latestBlock
    (blockChainActor ? BlockchainQuery).map { case chain: BlockChain =>
      assert(chain.getBlockchainSize == 2)
    }
  }

  it should "add transaction to the mined block" in new TestActor {
    val transaction = Transaction("Alice", "Bob", 10, 1920)
    blockChainActor ! Mine("Alice")
    Thread.sleep(5000)
    blockChainActor ! transaction

    blockChainActor ! Mine("Bob")
    Thread.sleep(5000)
    (blockChainActor ? BlockchainQuery).map { case chain: BlockChain =>
      assert(chain.latestBlock.transactions.contains(transaction))
    }
  }

  it should "send mined block from one actor to another" in new TestActor {

    blockChainActor ! AddPeer(blockChainActorTwo.toString())
    blockChainActor ! Mine("Alice")
    Thread.sleep(5000)

    (blockChainActorTwo ? BlockchainQuery).map { case chain: BlockChain =>
      assert(chain.getBlockchainSize == 1)
    }
  }

}
