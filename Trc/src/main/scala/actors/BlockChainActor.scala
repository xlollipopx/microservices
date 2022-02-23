package actors

import akka.actor.Props
import blockchain.{BlockChain, BlockchainNetwork, Mining}
import com.typesafe.scalalogging.Logger
import p2p.PeerToPeer

object BlockChainActor {
  def props( blockChain: BlockChain ): Props = Props(new BlockChainActor(blockChain))
}

class BlockChainActor( val blockChain: BlockChain ) extends CompositeActor with PeerToPeer
  with BlockchainNetwork with Mining {
  override val logger = Logger("logger")
}
