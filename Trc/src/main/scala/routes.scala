
import akka.actor.ActorRef
import akka.http.scaladsl.model.HttpEntity
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.unmarshalling.PredefinedFromEntityUnmarshallers
import akka.pattern.ask
import akka.util.Timeout

object routes {


  val simpleRoute: Route =
    path("home") { // DIRECTIVE
      complete(HttpEntity("HIII")) // DIRECTIVE
    }

}
