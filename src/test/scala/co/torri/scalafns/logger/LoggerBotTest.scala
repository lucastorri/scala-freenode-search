package co.torri.scalafns.logger

import akka.actor._
import akka.actor.Actor._
import akka.testkit._
import jerklib._
import jerklib.events._
import akka.util.duration._
import co.torri.scalafns._
import co.torri.scalafns.actor._
import org.specs2.specification.After
import jerklib.events.IRCEvent.Type._
import org.mockito.Mockito._
import org.mockito.Matchers._
import grizzled.slf4j.Logger


object IRCProtocolHandlerTest {
  val timeout = 1 second
}

class IRCProtocolHandlerTest extends ScalaFNSTest {

  "when receiving a new message" should {
    "not forward invalid messages" in new messageContext {
      irc.onMessage("#ruby", "martin: hello world")
      irc.onMessage("#scala", "martin: hello world", true)
      within(IRCProtocolHandlerTest.timeout) {
        received must be equalTo(false)
      }
    }
    "forward valid messages to the actor" in new messageContext {
      irc.onMessage("#scala", "martin: hello world")
      within(IRCProtocolHandlerTest.timeout) {
        received must be equalTo(true)
      }
    }
  }
  
  trait messageContext extends Context with TestKit with After {
    var received = false
    val actor = TestActorRef(new FakeActor(received = true)).start
    val irc = new IRCProtocolHandler("#scala", actor)
    
    def after =
      actor.stop
  }
  
  "when receiving a protocol event" should {
    "join channel" in new protocolContext {
      protocol.receiveEvent(connectEvent)
      
      there was one (session).join("#scala")
      there was one (session).setRejoinOnKick(true)
    }
    "receive message" in new protocolContext {
      channel.getName returns "#scala"
      protocol.receiveEvent(messageEvent)
      
      within(IRCProtocolHandlerTest.timeout) {
        received must be equalTo(true)
      }
    }
    "receive message" in new protocolContext {
      channel.getName returns "#scala"
      protocol.receiveEvent(privateMessage)
      
      within(IRCProtocolHandlerTest.timeout) {
        received must be equalTo(false)
      }
    }
  }
    
  trait protocolContext extends messageContext {
    val protocol = irc.protocolHandler
    val session = mock[Session]
    val channel = mock[jerklib.Channel]
    val connectEvent = new IRCEvent("NOTICE AUTH :*** Looking up your hostname", session, CONNECT_COMPLETE)
    val messageEvent = new MessageEvent(channel, "hello world", "raw data", session, CHANNEL_MESSAGE)
    val privateMessage = new MessageEvent(channel, "hello world", "raw data", session, PRIVATE_MESSAGE)
  }
    
}