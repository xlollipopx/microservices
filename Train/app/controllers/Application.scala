package controllers

import akka.actor.ActorSystem
import cats.effect.IO
import cats.effect.IO.fromFuture
import cats.effect.unsafe.implicits.global
import model.SchemaDefinition
import play.api.Configuration
import play.api.libs.json._
import play.api.mvc._
import repository.ProductRepository
import sangria.execution._
import sangria.marshalling.playJson._
import sangria.parser.{QueryParser, SyntaxError}
import sangria.slowlog.SlowLog

import javax.inject._
import scala.concurrent.Future
import scala.util.{Failure, Success}



class Application @Inject() (system: ActorSystem, config: Configuration, repository: ProductRepository) extends InjectedController {

  import system.dispatcher


  def index = Action {
    Ok(views.html.index())
  }
  def graphql(query: String, variables: Option[String], operation: Option[String]) = Action.async { request =>
    executeQuery(query, variables map parseVariables, operation, isTracingEnabled(request)).unsafeToFuture()
  }

  private def parseVariables(variables: String) =
    if (variables.trim == "" || variables.trim == "null") Json.obj() else Json.parse(variables).as[JsObject]


  def graphqlBody = Action.async(parse.json) { request =>
    val query = (request.body \ "query").as[String]
    val operation = (request.body \ "operationName").asOpt[String]

    val variables = (request.body \ "variables").toOption.flatMap {
      case JsString(vars) => Some(parseVariables(vars))
      case obj: JsObject => Some(obj)
      case _ => None
    }
    executeQuery(query, variables, operation, isTracingEnabled(request)).unsafeToFuture()
  }


  private def executeQuery(query: String, variables: Option[JsObject], operation: Option[String], tracing: Boolean) =
    QueryParser.parse(query) match {

      // query parsed successfully, time to execute it!
      case Success(queryAst) =>
        fromFuture[Result](IO(Executor.execute(SchemaDefinition.ProductSchema, queryAst, repository,
          operationName = operation,
          variables = variables getOrElse Json.obj(),
         // deferredResolver = DeferredResolver.fetchers(SchemaDefinition.products),
          exceptionHandler = exceptionHandler,
          queryReducers = List(
            QueryReducer.rejectMaxDepth[ProductRepository](15),
            QueryReducer.rejectComplexQueries[ProductRepository](4000, (_, _) => TooComplexQueryError)),
          middleware = if (tracing) SlowLog.apolloTracing :: Nil else Nil)
          .map(Ok(_))
          .recover {
            case error: QueryAnalysisError => BadRequest(error.resolveError)
            case error: ErrorWithResolver => InternalServerError(error.resolveError)
          }))

      // can't parse GraphQL query, return error
      case Failure(error: SyntaxError) =>
        fromFuture[Result](IO(Future.successful(BadRequest(Json.obj(
          "syntaxError" -> error.getMessage,
          "locations" -> Json.arr(Json.obj(
            "line" -> error.originalError.position.line,
            "column" -> error.originalError.position.column)))))))

      case Failure(error) =>
        fromFuture[Result](IO(throw error))
    }

  def isTracingEnabled(request: Request[_]) = request.headers.get("X-Apollo-Tracing").isDefined

  lazy val exceptionHandler = ExceptionHandler {
    case (_, error @ TooComplexQueryError) => HandledException(error.getMessage)
    case (_, error @ MaxQueryDepthReachedError(_)) => HandledException(error.getMessage)
  }
  case object TooComplexQueryError extends Exception("Query is too expensive.")


}
