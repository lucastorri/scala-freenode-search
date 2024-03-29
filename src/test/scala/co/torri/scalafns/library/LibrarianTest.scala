package co.torri.scalafns.library

import akka.testkit._
import akka.actor._
import akka.actor.Actor._
import java.net.URI

import akka.util.duration._
import akka.testkit._
import co.torri.scalafns.ScalaFNSTest
import co.torri.scalafns.actor._
import org.specs2.specification._


object LibrarianTest {
  val timeout = 1 second
}

class LibrarianTest extends ScalaFNSTest {

  "when receiving a message" should {
    "start to index the new log file" in new commonContext {
      librarian ! AddLog(logURI)
      
      within(LibrarianTest.timeout) {
        there was one (logsLibrary).addLog(any)
      }
    }
    
    "query the index" in new commonContext {
      val result = ChatLogSearchResult("a query")
      logsLibrary.searchLogs("a query") returns result
      
      val response = librarian !! Search("a query")
      result must be equalTo(result)
    }
  }

  trait commonContext extends Context with TestKit with After {
    val logURI = new URI("http://this.is.a.test/")
    val logsLibrary = mock[ChatLogsLibrary]
    val librarian = TestActorRef(new Librarian(logsLibrary)).start
    
    def after =
      librarian.stop
  }

}
