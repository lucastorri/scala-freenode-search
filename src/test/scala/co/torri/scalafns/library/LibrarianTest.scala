package co.torri.scalafns.library

import akka.testkit._
import akka.actor._
import akka.actor.Actor._
import java.net.URL

import akka.util.duration._
import akka.testkit._
import co.torri.scalafns.ScalaFNSTest
import co.torri.scalafns.actor._
import org.specs2.specification._


object LibrarianTest {
  val timeout = 1 second
}

class LibrarianTest extends ScalaFNSTest {

  "when receiving a message to add a log" should {
    "start to index the new log file" in new commonContext {
      librarian ! AddLog(logURL)
      
      within(LibrarianTest.timeout) {
        there was one (logsLibrary).addLog(any)
      }
    }
  }

  trait commonContext extends Context with TestKit with After {
    val logURL = new URL("http://this.is.a.test/")
    val logsLibrary = mock[ChatLogsLibrary]
    val librarian = TestActorRef(new Librarian(logsLibrary)).start
    
    def after =
      librarian.stop
  }

}
