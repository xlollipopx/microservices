package blockchain

import actors.CompositeActor
import blockchain.BlockChain.validBlock
import blockchain.BlockchainNetwork._
import p2p.PeerToPeer

object BlockchainNetwork {
  case object LastBlockQuery
  case object BlockchainQuery
  case class Balance(wallet: String)
  case class BlockchainResponse(chain: BlockChain)
  case class AddBlockRequest(block: Block)
  case class TransactionBroadcast(tc: Transaction)
  case class ReceivedTransaction(tc: Transaction)
}

trait BlockchainNetwork {
  this: PeerToPeer with CompositeActor =>

  receiver {
    case LastBlockQuery         => sender() ! blockChain.latestBlock
    case BlockchainQuery        => sender() ! blockChain
    case AddBlockRequest(block) => handleBlock(block)

    case Balance(wallet) => {
      log.info("money :" + blockChain.getBalanceForCurrentWallet(wallet))
      sender() ! blockChain.getBalanceForCurrentWallet(wallet).toString
    }

    case TransactionBroadcast(tc) => {
      blockChain.makePendingTransaction(tc.sender, tc.receiver, tc.amount)
      log.info(s"transaction added: ${blockChain.getPendingTransactions} , sender: ${tc.sender}")
      broadcast(ReceivedTransaction(tc))
    }
    case ReceivedTransaction(tc) => handleTransaction(tc)
  }

  def handleBlock(block: Block): Unit = block match {
    case block if validBlock(block, blockChain.latestBlock) =>
      blockChain.addBlock(block)
      log.info(s"Received new block from another node: $sender()")
    case _ => log.info(s"Invalid block from $sender()")
  }

  def handleTransaction(tc: Transaction): Unit = {
    log.info(s"Received new transaction $tc")
    blockChain.addTransactionToPending(tc)
  }

}
