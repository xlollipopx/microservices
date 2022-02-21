package p2p
import akka.actor.{Actor, ActorLogging, ActorRef, ActorSelection, Terminated}
import akka.pattern.pipe
import akka.util.Timeout
import actors.CompositeActor
import akka.actor.typed.Logger
import p2p.PeerToPeer.{AddPeer, GetPeers, HandShake, Peers, ResolvedPeer}
import scala.concurrent.ExecutionContextExecutor
import java.util.concurrent.TimeUnit
import scala.concurrent.duration.Duration



object PeerToPeer {

  case class AddPeer( address: String )

  case class ResolvedPeer( actorRef: ActorRef )

  case class Peers( peers: Seq[String] )

  case object GetPeers

  case object HandShake
}

trait PeerToPeer {
  this: CompositeActor =>

  implicit val timeout: Timeout = Timeout(Duration(5, TimeUnit.SECONDS))
  implicit val executionContext: ExecutionContextExecutor = context.system.dispatcher

  var peers: Set[ActorRef] = Set.empty

  def broadcast(message: Any): Unit ={
    peers.foreach(_ ! message)
  }

  receiver {
    case AddPeer(peerAddress) =>
      context.actorSelection(peerAddress).resolveOne().map(ResolvedPeer).pipeTo(self)
    case ResolvedPeer(networkPeerRef) =>
      if(!peers.contains(networkPeerRef)) {
        networkPeerRef ! HandShake
        peers += networkPeerRef
        networkPeerRef ! GetPeers
      }

    case HandShake =>
      peers += sender()

    case GetPeers =>
      sender() ! Peers(peers.toSeq.map(_.path.toSerializationFormat))

    case Peers(seq) => seq.foreach(self ! AddPeer(_))

  }



}


