package blockchain

import actors.CompositeActor
import blockchain.BlockChain.validBlock
import blockchain.BlockchainNetwork.{
  AddBlockRequest,
  BlockchainQuery,
  BlockchainResponse,
  LastBlockQuery,
  ReceivedTransaction,
  TransactionBroadcast
}
import com.typesafe.scalalogging.Logger
import p2p.PeerToPeer

object BlockchainNetwork {
  case object LastBlockQuery
  case object BlockchainQuery
  case class BlockchainResponse(chain: BlockChain)
  case class AddBlockRequest(block: Block)
  case class TransactionBroadcast(tc: Transaction)
  case class ReceivedTransaction(tc: Transaction)
}
trait BlockchainNetwork {
  this: PeerToPeer with CompositeActor =>
  val logger:     Logger
  val blockChain: BlockChain

  receiver {
    case LastBlockQuery           => sender() ! blockChain.latestBlock
    case BlockchainQuery          => sender() ! blockChain
    case AddBlockRequest(block)   => handleBlock(block)
    case TransactionBroadcast(tc) => broadcast(ReceivedTransaction(tc))
    case ReceivedTransaction(tc)  => handleTransaction(tc)
  }

  def handleBlock(block: Block): Unit = block match {
    case block if validBlock(block, blockChain.latestBlock) =>
      blockChain.addBlock(block)
      logger.info(s"Received new block from another node: $sender()")
    case _ => logger.info(s"Invalid block from $sender()")
  }

  def handleTransaction(tc: Transaction): Unit = {
    logger.info(s"Received new transaction $tc")
    blockChain.addTransactionToPending(tc)
  }

}
