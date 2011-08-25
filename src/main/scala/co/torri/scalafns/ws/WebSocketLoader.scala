package co.torri.scalafns.ws

import javax.servlet._
import javax.servlet.http._
import org.webbitserver._
import javax.naming.InitialContext

class WebSocketLoader extends ServletContextListener {

  private var _webSocketServer: WebServer = _

  override def contextInitialized(e: ServletContextEvent): Unit = {
    val context = e.getServletContext
    val url = context.getInitParameter("websocketSearchService")
    val port = context.getInitParameter("websocketSearchServicePort").toInt
    _webSocketServer = WebServers.createWebServer(port)
    _webSocketServer.add(url, SearchWebSocket).start
  }

  override def contextDestroyed(e: ServletContextEvent): Unit =
    _webSocketServer.stop

}
