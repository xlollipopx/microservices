package p2p

import actors.CompositeActor
import akka.actor.ActorRef
import akka.pattern.pipe
import akka.util.Timeout
import blockchain.{Block, BlockChain}
import blockchain.BlockchainNetwork.{BlockchainIncome, BlockchainQuery, BlocksIncome, BlocksQuery}
import p2p.PeerToPeer._

import java.util.concurrent.TimeUnit
import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration.Duration

object PeerToPeer {

  case class AddPeer(address: String)

  case class ResolvedPeer(actorRef: ActorRef)

  case class Peers(peers: Seq[String])

  case object GetPeers

  case object HandShake
}

trait PeerToPeer {
  this: CompositeActor =>

  protected var blockChain: BlockChain

  implicit val timeout:          Timeout                  = Timeout(Duration(5, TimeUnit.SECONDS))
  implicit val executionContext: ExecutionContextExecutor = context.system.dispatcher

  var peers: Set[ActorRef] = Set.empty

  def broadcast(message: Any): Unit = {
    peers.foreach(_ ! message)
  }

  receiver {
    case AddPeer(peerAddress) =>
      context.actorSelection(peerAddress).resolveOne().map(ResolvedPeer).pipeTo(self)
    case ResolvedPeer(networkPeerRef) =>
      if (!peers.contains(networkPeerRef) && networkPeerRef != context.self) {
        networkPeerRef ! HandShake
        peers += networkPeerRef
        networkPeerRef ! GetPeers
        networkPeerRef ! BlocksQuery
      }

    case HandShake =>
      peers += sender()

    case GetPeers =>
      sender() ! Peers(peers.toSeq.map(_.path.toSerializationFormat))

    case Peers(seq) =>
      log.info(s"ADDING PEERS ${seq}")
      seq.foreach(self ! AddPeer(_))

    case BlockchainIncome(bc) =>
      log.info(s"Receive blockchain, length: ${bc.getBlockchainSize}")
      if (bc.getBlockchainSize > blockChain.getBlockchainSize && validateNewBlockChain(blockChain, bc.allBlocks)) {

        bc.allBlocks
          .slice(blockChain.getBlockchainSize, bc.getBlockchainSize)
          .foreach(el => blockChain.addBlock(el))

        log.info("New size: " + blockChain.getBlockchainSize)

        sender ! s"Chain updated, new size: ${blockChain.getBlockchainSize}"
      }

    case BlocksIncome(bc) =>
      log.info(s"Receive blockchain, length: ${bc.length}")
      if (bc.length > blockChain.getBlockchainSize && validateNewBlockChain(blockChain, bc)) {
        bc
          .slice(blockChain.getBlockchainSize, bc.length)
          .foreach(el => blockChain.addBlock(el))

        log.info("New size: " + blockChain.getBlockchainSize)

        sender ! s"Chain updated, new size: ${blockChain.getBlockchainSize}"
      }
  }

  def validateNewBlockChain(oldChain: BlockChain, newChain: Seq[Block]): Boolean = {

    val remains = newChain
      .slice(oldChain.getBlockchainSize, newChain.length)
      .map(_.previousHash)

    val remainsPrev = newChain
      .slice(oldChain.getBlockchainSize - 1, newChain.length - 1)
      .map(_.hash)

    log.info(remains + " " + remainsPrev)
    remains == remainsPrev

  }

}
