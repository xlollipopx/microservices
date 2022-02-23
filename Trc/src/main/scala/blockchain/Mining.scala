package blockchain

import actors.CompositeActor
import blockchain.BlockchainNetwork.AddBlockRequest
import blockchain.Mining.Mine
import p2p.PeerToPeer

object Mining {

  case class Mine(data: String)

}

trait Mining {
  this: CompositeActor with PeerToPeer with BlockchainNetwork =>
  receiver { case Mine(data) =>
    blockChain.addBlock(data)
    broadcast(AddBlockRequest(blockChain.latestBlock))
    logger.info("Block mined")
    sender() ! blockChain.latestBlock

  }
}
