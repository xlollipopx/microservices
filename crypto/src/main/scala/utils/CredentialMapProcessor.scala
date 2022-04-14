package utils
import model.login
import model.login.Credentials

class CredentialMapProcessor extends CredentialProcessor {

  val map = collection.mutable.Map[String, String]()

  override def getCredentials: Option[login.Credentials] = map.get("username").map(Credentials(_, map("password")))

  override def setCredentials(credentials: login.Credentials): Unit = {
    map.put("username", credentials.username)
    map.put("password", credentials.password)
  }

  override def getSecretKey: String = map("secretKey")

  override def setSecretKey(key: String): Unit = if (!map.contains("secretKey")) {
    map.put("secretKey", key)
  }

}
