package co.torri.scalafns.logger

import org.jibble.pircbot._
import akka.actor._


class IRCProtocolHandler(server: String, channel: String) extends PircBot {
  
  private var _actorOption: Option[ActorRef] = None
  
  def actor_=(actor: ActorRef) =
    _actorOption = Option(actor)
  
  def actor =
    _actorOption
  
  override def onMessage(channel: String, sender: String, login: String, hostname: String, msg: String) =
    if (this.channel == channel && hostname == server) _actorOption.map(_ ! sender + ": " + msg)
}

class LoggerBot(irc: PircBot) extends Actor {

  def receive = {
    case _ => {}
  }
  
}
