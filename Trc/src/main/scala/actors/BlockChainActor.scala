package actors

import akka.actor.Props
import blockchain.{BlockChain, BlockchainNetwork, Mining}
import p2p.PeerToPeer

object BlockChainActor {
  def props( blockChain: BlockChain ): Props = Props(new BlockChainActor(blockChain))
}

class BlockChainActor( var blockChain: BlockChain ) extends CompositeActor with PeerToPeer
  with BlockchainNetwork with Mining {
}
