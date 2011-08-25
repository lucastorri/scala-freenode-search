package co.torri.scalafns.logger

import jerklib._;
import akka.actor.Actor._


class LibrarianLoader extends ServletContextListener {
  
  private val _manager = new ConnectionManager(new Profile("logbot"))
  
  override def contextInitialized(e: ServletContextEvent): Unit = {
    val session = _manager.requestConnection("irc.freenode.net")
    session.addIRCEventListener(new IRCProtocolHandler("#scala", actorOf[LoggerBot]))
  }
    
  override def contextDestroyed(e: ServletContextEvent) =
    _manager.quit
}
