package co.torri.scalafns.ws

import javax.servlet._
import javax.servlet.http._
import org.webbitserver._

class WebSocketLoader extends ServletContextListener {

  private lazy val _webSocketServer = WebServers.createWebServer(8123)

  override def contextInitialized(e: ServletContextEvent): Unit =
    _webSocketServer.add("/search", SearchWebSocket).start

  override def contextDestroyed(e: ServletContextEvent): Unit =
    _webSocketServer.stop

}
