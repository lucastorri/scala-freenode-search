package co.torri.scalafns.logger

import akka.actor._
import jerklib._
import jerklib.events._
import jerklib.events.IRCEvent.Type._
import jerklib.listeners.IRCEventListener


class IRCProtocolHandler(_server: String, _channel: String, _bot: ActorRef) {
  
  def onMessage(channel: String, msg: String, pvt: Boolean = false) =
    if (_channel == channel && !pvt) _bot ! msg
    
  private def _onMessage(msgEvent: MessageEvent) =
    onMessage(msgEvent.getChannel.getName, msgEvent.getMessage, msgEvent.isPrivate)

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

class LoggerBot(irc: IRCProtocolHandler) extends Actor {
  
  def receive = {
    case _ => {}
  }
  
}
