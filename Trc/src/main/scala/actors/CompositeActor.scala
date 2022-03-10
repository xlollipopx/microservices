package actors
import akka.actor.{Actor, ActorLogging}
import akka.actor.Actor.emptyBehavior

class CompositeActor extends Actor with ActorLogging {
  var receivers: Receive = emptyBehavior

  override def receive: Receive = receivers

  def receiver(next: Receive): Unit = { receivers = receivers orElse next }
}
