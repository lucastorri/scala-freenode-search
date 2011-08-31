package co.torri.scalafns

import javax.servlet._
import javax.servlet.http._
import akka.actor.Actor._
import akka.actor._
import java.io.File
import java.io.File.{ separator => | }
import org.apache.lucene.store._
import co.torri.scalafns.library._
import co.torri.scalafns.logger._
import co.torri.scalafns.ws._
import jerklib._
import grizzled.slf4j._
import org.webbitserver._


class Loader extends ServletContextListener with Logging {
  
  private val _manager = new ConnectionManager(new Profile("logbot"))
  private var _webSocketServer: WebServer = _
  
  override def contextInitialized(e: ServletContextEvent): Unit = {
    val logFolder = new File (e.getServletContext.getResource(|).getFile + "log" + | + "irc")
    logFolder.mkdirs
    val indexFolderPath = e.getServletContext.getResource(|).getFile + "WEB-INF" + | + "lucene"
    new File(indexFolderPath).mkdirs
    
    //start librarian
    val factory = new IndexFactory(FSDirectory.open(new File(indexFolderPath)))
    val library = new ChatLogsLibrary(factory)
    val librarian = actorOf(new Librarian(library)).start
    logger.warn("Waking up Librarian")
    
    //start websocket
    val context = e.getServletContext
    val url = context.getInitParameter("websocketSearchService")
    val port = context.getInitParameter("websocketSearchServicePort").toInt
    _webSocketServer = WebServers.createWebServer(port)
    _webSocketServer.add(url, new SearchWebSocket(librarian)).start
    logger.warn("Listening on WebSocket")
    
    //start logger
    val session = _manager.requestConnection("localhost")//"irc.freenode.net")
    var ircLogger = MessageLogger(logFolder)
    session.addIRCEventListener(new IRCProtocolHandler("#scala", actorOf(new LoggerBot(ircLogger)).start).protocolHandler)
    logger.warn("Connecting to chat")
    
    //start scheduler
    actorOf(new LogMonitor(librarian, logFolder)).start
    logger.warn("Monitoring the chat logs")
  }

  override def contextDestroyed(e: ServletContextEvent) = {
    _manager.quit
    _webSocketServer.stop
  }


  private class LogMonitor(_librarian: ActorRef, logFolder: File) extends Actor with Logging {
    self.receiveTimeout = Some(10000L)
    
    private var _logFiles = Map[String, Long]()
    
    def receive = {
      case ReceiveTimeout => {
        _logFiles = 
          logFolder.
            listFiles.
              map { f =>
                val (filepath, lastModified) = (f.getCanonicalPath, f.lastModified)
                _logFiles.get(filepath) match {
                  case Some(oldLastModified) if lastModified == oldLastModified  => {}
                  case _ => _librarian ! AddLog(f.toURI)
                }
                (filepath, lastModified)
              }.
              toMap
      }
    }
    
  }
  
}