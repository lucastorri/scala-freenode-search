package co.torri.scalafns.ws
import org.webbitserver._

object SearchWebSocket extends WebSocketHandler {

  private var cCount = 0

  override def onOpen(c: WebSocketConnection) {
    c.send("Hello! There are " + cCount + " other cs active")
    cCount+=1
  }

  override def onClose(c: WebSocketConnection) =
    cCount-=1

  override def onMessage(c: WebSocketConnection, msg: String): Unit =
    c.send(msg.toUpperCase())

  override def onMessage(c: WebSocketConnection, msg: Array[Byte]) {}

  override def onPong(c: WebSocketConnection, msg: String) {}

}
