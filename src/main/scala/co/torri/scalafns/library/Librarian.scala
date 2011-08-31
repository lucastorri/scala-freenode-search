package co.torri.scalafns.library

import co.torri.scalafns.library._

import akka.actor._
import java.util.Date
import java.net.URI


class Librarian(_library: ChatLogsLibrary) extends Actor {

  def receive = {
    case AddLog(uri) => _library addLog ChatLog(uri)
    case Search(query) => self reply(_library searchLogs query)
    case _ => {}
  }

}

case class AddLog(logURI: URI)

case class Search(query: String)