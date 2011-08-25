package co.torri.scalafns.logger

import akka.actor._
import akka.actor.Actor._
import akka.testkit.TestKit
import org.jibble.pircbot._

import akka.util.duration._
import co.torri.scalafns.ScalaFNSTest
import co.torri.scalafns.actor._
import org.specs2.specification._

object IRCProtocolHandlerTest {
  val timeout = 1 second
}

class IRCProtocolHandlerTest extends ScalaFNSTest {
  
  "when receiving a new message" should {
    "not forward invalid messages" in new normalContext {
      irc.onMessage("#scala", "someone", "someone", "irc.wrongserver.net", "hello world")
      irc.onMessage("#ruby", "someone", "someone", "irc.freenode.net", "hello world")
      within(IRCProtocolHandlerTest.timeout) {
        received must be equalTo(false)
      }
    }
    "forward valid messages to the actor" in new normalContext {
      irc.onMessage("#scala", "someone", "someone", "irc.freenode.net", "hello world")
      within(IRCProtocolHandlerTest.timeout) {
        received must be equalTo(true)
      }
    }
  }
  
  trait normalContext extends Context with TestKit with After {
    var received = false
    val irc = new IRCProtocolHandler("irc.freenode.net", "#scala")
    val actor = actorOf(new FakeActor(received = true)).start
    irc.actor = actor
    
    def after =
      actor.stop
  }
  
}

class LoggerBotTest extends ScalaFNSTest {
  
  "when connecting to a channel" should {
    "change its nick" in new normalContext {
      there was one (irc).changeNick("logbot")
    }
  }
  
  trait normalContext extends Context {
    val irc = mock[PircBot]
    val bot = actorOf(new LoggerBot(irc)).start
  }
}