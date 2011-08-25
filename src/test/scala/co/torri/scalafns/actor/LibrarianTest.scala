package co.torri.scalafns.actor

import co.torri.scalafns.library._

import akka.testkit._
import akka.actor._
import akka.actor.Actor._
import java.net.URL

import co.torri.scalafns.ScalaFNSTest


class LibrarianTest extends ScalaFNSTest {

  "when receiving a message to add a log" should {
    "start to index the new log file" in new commonContext {
      val msg = AddLog(logURL)
      librarian ! msg
      //XXX use akka testkit
      Thread.sleep(100)
      
      there was one (logsLibrary).addLog(ChatLog(logURL))
    }
  }

  trait commonContext extends Context {
    val logURL = new URL("http://this.is.a.test/")
    val logsLibrary = mock[ChatLogsLibrary]
    val librarian = actorOf(new Librarian(logsLibrary)).start
  }

}
