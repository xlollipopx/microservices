package utils

import model.login.Credentials

trait CredentialProcessor {
  def getCredentials: Option[Credentials]
  def setCredentials(credentials: Credentials): Unit
  def getSecretKey: String
  def setSecretKey(key: String): Unit

}
