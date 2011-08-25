package co.torri.scalafns.library

import co.torri.scalafns.library._

import akka.actor._
import java.util.Date
import java.net.URL


class Librarian(_library: ChatLogsLibrary) extends Actor {

  def receive = {
    case AddLog(url) => _library addLog ChatLog(url)
    case _ => {}
  }

}

case class AddLog(logURL: URL)
