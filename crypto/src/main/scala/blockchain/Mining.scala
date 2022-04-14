package blockchain

import actors.CompositeActor
import akka.actor.Props
import blockchain.BlockchainNetwork.{AddBlockRequest, BlockchainIncome, BlocksIncome}
import blockchain.Mining.Mine
import p2p.PeerToPeer

import scala.concurrent.Future
import scala.util.{Failure, Success}

trait Mining {

  this: CompositeActor with PeerToPeer with BlockchainNetwork =>

  receiver { case Mine(data) =>
    if (!BlockChain.isMining) {
      Future(blockChain.addBlock(data)).onComplete {
        case Success(_) =>
          broadcast(AddBlockRequest(blockChain.latestBlock))
          broadcast(BlocksIncome(blockChain.allBlocks))
          log.info("Block mined!")
        case Failure(exception) => log.info(exception.toString)
      }
      sender() ! "Mining started!"
    } else {
      sender() ! "Mining has already started!"
    }

  }
}

object Mining {

  case class Mine(data: String)

}
