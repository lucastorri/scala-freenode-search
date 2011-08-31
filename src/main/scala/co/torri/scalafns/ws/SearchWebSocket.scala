package co.torri.scalafns.ws

import org.webbitserver._
import akka.actor._
import akka.actor.Actor._
import co.torri.scalafns.library._
import grizzled.slf4j._
import co.torri.jsonr._


class SearchWebSocket(_librarian: ActorRef) extends WebSocketHandler with Logging {

  override def onOpen(c: WebSocketConnection) = {}

  override def onClose(c: WebSocketConnection) = {}

  override def onMessage(c: WebSocketConnection, msg: String): Unit = try {
    _librarian !! Search(msg) match {
      case Some(results: ChatLogSearchResult) => c.send(WebResult(results).toJSON.toString)
      case _ => c.send($("error" -> "We got a problem over here. Sorry about that.").toString)
    }
  } catch { case e => logger.error("Problem", e) }

  override def onMessage(c: WebSocketConnection, msg: Array[Byte]) {}

  override def onPong(c: WebSocketConnection, msg: String) {}

}

case class WebResult(query: String, hits: Int, files: Seq[String])

object WebResult {
  
  def apply(r: ChatLogSearchResult): WebResult =
    apply(r.query, r.totalHits, r.chatLogs.map{ log =>
      val path = log.uri.getPath
      path.substring(path.indexOf("/log/irc"))
    })
  
}
