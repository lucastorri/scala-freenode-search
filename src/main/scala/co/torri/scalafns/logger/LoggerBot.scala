package co.torri.scalafns.logger

import java.util.Date
import java.text.SimpleDateFormat
import akka.actor._
import jerklib._
import jerklib.events._
import jerklib.events.IRCEvent.Type._
import jerklib.listeners.IRCEventListener
import java.io.PrintWriter
import grizzled.slf4j._
import java.io.File
import java.io.File.{ separator => | }


class IRCProtocolHandler(_channel: String, _bot: ActorRef) {
  
  def onMessage(channel: String, sender: String, msg: String, pvt: Boolean = false, date: Date = new Date) =
    if (_channel == channel && !pvt) _bot ! IRCMessage(channel, sender, msg, date)
    
  private def _onMessage(msgEvent: MessageEvent) =
    onMessage(msgEvent.getChannel.getName, msgEvent.getNick, msgEvent.getMessage, msgEvent.isPrivate)

  object protocolHandler extends IRCEventListener {

    override def receiveEvent(e: IRCEvent): Unit =
      e.getType match {
        case CONNECT_COMPLETE => {
          val session = e.getSession
          session.join(_channel)
          session.setRejoinOnKick(true)
        }
        case CHANNEL_MESSAGE => _onMessage(e.asInstanceOf[MessageEvent])
        case _ => {}
      }

  }
}

case class MessageLogger(dir: File) extends Logging {
  require(dir.isDirectory)
  
  private val _logFileFormat = "%s_%s.log"
  private val _logFileDateFormat = new SimpleDateFormat("yyyy-MM-dd")
  private val _logException: PartialFunction[Throwable, Unit] = { case e => logger.error("Error logging chat messages", e) }
  private var _currentFile: (String, Option[PrintWriter]) = ("", None)
  
  private def _logFileFor(msg: IRCMessage) =
    (dir.getCanonicalPath + | + 
      _logFileFormat.
        format(msg.channel, _logFileDateFormat.format(msg.time))).replaceAll("#", "")
    
  
  private def _loggerFor(msg: IRCMessage) = {
    val filename = _logFileFor(msg)
    if (_currentFile._1 != filename) {
      _currentFile._2.map { printer =>
        try printer.close
        catch _logException
      }
      try _currentFile = (filename, Some(new PrintWriter(filename)))
      catch _logException
    }
    _currentFile._2.get
  }
  
  def apply(msg: IRCMessage) =
    try {
      val logger = _loggerFor(msg)
      logger.println(msg)
      logger.flush
    } catch _logException
  
}

class LoggerBot(log: MessageLogger) extends Actor {
  
  def receive = {
    case m: IRCMessage => { log(m) }
    case _ => {}
  }
  
}

object IRCMessage {
  
  private val _timeFormat = new SimpleDateFormat("[yyyy-MM-dd/HH:mm:ss]")
  
  def formatTime(msg: IRCMessage) =
    _timeFormat.format(msg.time)
  
}

case class IRCMessage(channel: String, sender: String, msg: String, time: Date) {
  
  override def toString =
    IRCMessage.formatTime(this) + " " + sender + ": " + msg
  
}