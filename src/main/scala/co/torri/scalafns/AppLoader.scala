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
import org.streum.configrity._
import scala.io.Source._


class AppLoader extends ServletContextListener with Logging {
  
  private var _manager: ConnectionManager = _
  private var _webSocketServer: WebServer = _
  
  override def contextInitialized(e: ServletContextEvent): Unit = try {
    
    val _config =
      try Configuration.load(fromURL(getClass.getClassLoader.getResource("scala-freenode-search.conf")))
      catch {
        case e => logger.error("Could not load conf file", e); return
      }

    val logDir = new File (System.getenv("OPENSHIFT_DATA_DIR") + _config[String]("logbot.outputfolder"))
    val indexDir = new File(System.getenv("OPENSHIFT_DATA_DIR") + _config[String]("librarian.indexfolder"))
    val websocketService =  _config[String]("websocket.searchservice")
    val websocketPort = _config[Int]("websocket.port")
    val ircServer = _config[String]("logbot.server")
    val ircChannel = _config[String]("logbot.channel")
    val ircNick = _config[String]("logbot.name")
    val monCheckInterval = _config[Long]("logmon.checkinterval")
    
    logDir.mkdirs
    indexDir.mkdirs
    
    
    val factory = new IndexFactory(FSDirectory.open(indexDir))
    val library = new ChatLogsLibrary(factory)
    val librarian = actorOf(new Librarian(library)).start
    logger.debug("Waking up Librarian")
    
    
    _webSocketServer = WebServers.createWebServer(websocketPort)
    _webSocketServer.add(websocketService, new SearchWebSocket(librarian)).start
    logger.debug("Listening on WebSocket")
    
    
    _manager = new ConnectionManager(new Profile(ircNick))
    val session = _manager.requestConnection(ircServer)
    var ircLogger = MessageLogger(logDir)
    session.addIRCEventListener(new IRCProtocolHandler(ircChannel, actorOf(new LoggerBot(ircLogger)).start).protocolHandler)
    logger.debug("Connecting to chat")
    
    
    actorOf(new LogMonitor(librarian, logDir, monCheckInterval)).start
    logger.debug("Monitoring the chat logs")
    
  } catch { case e => logger.error("Could not start App", e); e.printStackTrace}

  override def contextDestroyed(e: ServletContextEvent) = {
    _manager.quit
    _webSocketServer.stop
    registry.shutdownAll
  }


  private class LogMonitor(_librarian: ActorRef, logDir: File, checkInterval: Long) extends Actor with Logging {
    self.receiveTimeout = Some(checkInterval)
    
    private var _logFiles = Map[String, Long]()
    
    def receive = {
      case ReceiveTimeout => {
        _logFiles = 
          logDir.
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