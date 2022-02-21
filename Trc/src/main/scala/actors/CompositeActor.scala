package actors
import akka.actor.Actor
import akka.actor.Actor.emptyBehavior

class CompositeActor extends Actor {
  var receivers: Receive = emptyBehavior
  override def receive: Receive = receivers
   def receiver(next: Receive): Unit =  {receivers = receivers orElse next}
}
