package co.torri.scalafns.ws

import javax.servlet._
import javax.servlet.http._
import org.webbitserver._

class WebSocketLoader extends ServletContextListener {

  private val webSocketServer = WebServers.createWebServer(8123)

  override def contextInitialized(e: ServletContextEvent): Unit = {
    println("loading web sockets server")
    webSocketServer.add("/search", SearchWebSocket).start
  }

  override def contextDestroyed(e: ServletContextEvent): Unit =
    webSocketServer.stop();
}
