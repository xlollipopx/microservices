package http

import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.http.scaladsl.server
import akka.http.scaladsl.server.Directive1
import akka.http.scaladsl.server.Directives.{complete, optionalHeaderValueByName, provide}
import pdi.jwt.{JwtAlgorithm, JwtClaim, JwtJson4s}
import utils.FileProcessor

import java.util.Random;
import java.util.concurrent.TimeUnit
import javax.crypto.SecretKey
import scala.util.{Failure, Success, Try}

class Authentication(fileProcessor: FileProcessor) {

  val algorithm = JwtAlgorithm.HS256

  def checkPassword(username: String, password: String): Boolean = {
    val credentials = fileProcessor.getCredentials
    credentials match {
      case Some(c) => c.username == username && c.password == password
      case None    => false
    }
  }

  def generateSecretKey(): String = {
    val random = new Random();
    val str    = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
    Seq
      .range(1, 30)
      .foldLeft("")((acc, _) => {
        val idx = random.nextInt(62)
        acc :+ str.charAt(idx)
      })
  }

  def createToken(username: String, expirationPeriodInDays: Int, secretKey: String): String = {
    val claims = {
      JwtClaim(
        expiration = Some(System.currentTimeMillis() / 1000 + TimeUnit.DAYS.toSeconds(expirationPeriodInDays)),
        issuedAt   = Some(System.currentTimeMillis() / 1000),
        issuer     = Some(username)
      )
    }
    JwtJson4s.encode(claims, secretKey, algorithm)
  }

  def isTokenExpired(token: String): Boolean =
    JwtJson4s.decode(token, fileProcessor.getSecretKey, Seq(algorithm)) match {
      case Success(claims) => claims.expiration.getOrElse(0L) < System.currentTimeMillis() / 1000
      case Failure(_)      => true
    }

  def isTokenValid(token: String): Boolean = JwtJson4s.isValid(token, fileProcessor.getSecretKey, Seq(algorithm))

  def authenticated: Directive1[String] = {

    optionalHeaderValueByName("Authorization").flatMap {
      case Some(token) if isTokenExpired(token) =>
        complete(StatusCodes.Unauthorized -> "Session expired!")

      case Some(token) if isTokenValid(token) =>
        provide(JwtJson4s.decode(token, fileProcessor.getSecretKey, Seq(JwtAlgorithm.HS256)).get.issuer.get)

      case _ => complete(StatusCodes.Unauthorized -> "Invalid Token")
    }
  }

}
