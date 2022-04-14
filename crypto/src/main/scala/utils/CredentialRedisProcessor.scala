package utils

import com.redis.RedisClient
import model.login

class CredentialRedisProcessor(client: RedisClient) extends CredentialProcessor {

  override def getCredentials: Option[login.Credentials] =
    for {
      u <- client.get("username")
      p <- client.get("password")
    } yield (login.Credentials(u, p))

  override def setCredentials(credentials: login.Credentials): Unit = {
    client.set("username", credentials.username)
    client.set("password", credentials.password)
  }

  override def getSecretKey: String = {
    client.get("secretKey").get
  }

  override def setSecretKey(key: String): Unit = {
    if (client.get("secretKey").isEmpty)
      client.set("secretKey", key)
  }
}
