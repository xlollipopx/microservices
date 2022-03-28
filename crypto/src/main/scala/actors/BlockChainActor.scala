package actors

import akka.actor.Props
import blockchain.{BlockChain, BlockchainNetwork, Mining}
import p2p.PeerToPeer
import akka.actor.Props

class BlockChainActor private (var blockChain: BlockChain)
  extends CompositeActor
    with PeerToPeer
    with BlockchainNetwork
    with Mining {}

object BlockChainActor {
  def props(blockChain: BlockChain): Props = Props(new BlockChainActor(blockChain))
}
