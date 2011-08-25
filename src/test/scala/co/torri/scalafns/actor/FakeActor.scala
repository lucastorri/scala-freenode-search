package co.torri.scalafns.actor

import akka.actor._


class FakeActor(_handle: => Unit) extends Actor {
  
  def receive = {
    case _ => _handle
  }
  
}