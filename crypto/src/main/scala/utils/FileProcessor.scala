package utils

import model.login.Credentials

import java.io.{FileInputStream, FileOutputStream, OutputStream}
import java.util.Properties
import scala.io.Source
import scala.util.{Failure, Success, Try}

class FileProcessor {

  val fileName = "src/main/resources/credentials.config"

  val prop: Properties = new Properties()

  def loadSteam(): Unit = {
    val currentThread      = Thread.currentThread
    val contextClassLoader = currentThread.getContextClassLoader
    val propertiesStream   = contextClassLoader.getResourceAsStream("credentials.config")

    if (propertiesStream != null) {
      prop.load(propertiesStream)
    } else {}
  }

  def getCredentials: Option[Credentials] = {
    loadSteam()

    prop.getProperty("username") match {
      case null => None
      case login: String => Some(Credentials(login, prop.getProperty("password")))
    }

  }

  def setCredentials(credentials: Credentials): Unit = {
    loadSteam()
    prop.setProperty("username", credentials.username)
    prop.setProperty("password", credentials.password)
    val out: OutputStream = new FileOutputStream(fileName);
    prop.store(out, "");
  }

  def getSecretKey: String = {
    loadSteam()
    prop.getProperty("secretKey")
  }

  def setSecretKey(key: String): Unit = {
    loadSteam()
    if (!prop.containsKey("secretKey")) {
      prop.setProperty("secretKey", key)
      val out: OutputStream = new FileOutputStream(fileName)
      prop.store(out, "")
    }
  }
}
