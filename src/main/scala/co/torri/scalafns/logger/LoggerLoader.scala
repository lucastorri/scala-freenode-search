package co.torri.scalafns.logger

import javax.servlet._
import javax.servlet.http._
import akka.actor._
import akka.actor.Actor._
import jerklib._
import akka.actor.Actor._
import grizzled.slf4j._
import java.io._
import co.torri.scalafns.library._


class LoggerLoader extends ServletContextListener {
  
  private val _manager = new ConnectionManager(new Profile("logbot"))
  
  override def contextInitialized(e: ServletContextEvent): Unit = {
    val session = _manager.requestConnection("irc.freenode.net")
    session.addIRCEventListener(new IRCProtocolHandler("#scala", actorOf[LoggerBot].start).protocolHandler)
    uglyWorkaroundThatWorksOnMyMachine
  }
  
  private def uglyWorkaroundThatWorksOnMyMachine = {
    Thread.sleep(10000)
    try {
      var librarian: ActorRef = null
      do {
        librarian = remote.actorFor("librarian", "localhost", 9123)
      } while (librarian == null)
      val logFilesFolder = new File("/Users/lucastorri/Projects/scala-freenode-search/log/irc")
      while(true) {
        val currentLog = logFilesFolder.listFiles.sortBy(_.lastModified).last.toURI
        librarian ! AddLog(currentLog)
        Thread.sleep(60000)
      }
    } catch { case e => e.printStackTrace }
  }
    
  override def contextDestroyed(e: ServletContextEvent): Unit =
    _manager.quit
}
