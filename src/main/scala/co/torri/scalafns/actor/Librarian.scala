package co.torri.scalafns.actor

import co.torri.scalafns.library._

import akka.actor._
import java.util.Date
import java.net.URL


class Librarian(library: ChatLogsLibrary) extends Actor {

  def receive = {
    case AddLog(url) => library addLog ChatLog(url)
    case _ => {}
  }

}

case class AddLog(logURL: URL)
