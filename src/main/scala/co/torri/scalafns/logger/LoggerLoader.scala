package co.torri.scalafns.logger

import javax.servlet._
import javax.servlet.http._
import akka.actor.Actor._
import jerklib._
import akka.actor.Actor._
import grizzled.slf4j._


class LoggerLoader extends ServletContextListener {
  
  private val _manager = new ConnectionManager(new Profile("logbot"))
  
  override def contextInitialized(e: ServletContextEvent): Unit = {
    val session = _manager.requestConnection("irc.freenode.net")
    session.addIRCEventListener(new IRCProtocolHandler("#scala", actorOf[LoggerBot].start).protocolHandler)
  }
    
  override def contextDestroyed(e: ServletContextEvent): Unit =
    _manager.quit
}
