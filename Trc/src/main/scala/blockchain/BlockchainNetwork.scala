package blockchain

import actors.CompositeActor
import blockchain.BlockChain.validBlock
import blockchain.BlockchainNetwork.{AddBlockRequest, BlockchainQuery, BlockchainResponse, LastBlockQuery}
import p2p.PeerToPeer


object BlockchainNetwork {
  case object LastBlockQuery
  case object BlockchainQuery
  case class BlockchainResponse(chain: BlockChain)
  case class AddBlockRequest(block: Block)
}
trait BlockchainNetwork {
  this: PeerToPeer with CompositeActor =>

  var blockChain: BlockChain
  receiver {
    case LastBlockQuery => sender() ! blockChain.latestBlock
    case BlockchainQuery => sender() ! blockChain
    case AddBlockRequest(block) => handleBlock(block)

  }

  def handleBlock(block: Block): Unit = block match {
    case block if validBlock(block, blockChain.latestBlock) =>
      blockChain.addBlock(block)
    case _ =>
  }

}

