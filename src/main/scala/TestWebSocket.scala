/*package test

import org.eclipse.jetty.websocket._
import org.eclipse.jetty.websocket.WebSocket._
import javax.servlet.http._

class MyWebSocketServlet extends WebSocketServlet {

    override def doWebSocketConnect(request: HttpServletRequest, protocol: String): WebSocket = {
        println("creating web socket")
        return new ProxyWebSocket(request.getPathInfo());
    }	

	class ProxyWebSocket(val uri : String) extends WebSocket.OnTextMessage {

	        def onConnect(connection: Connection) {
	        	println("Client connected to WebSocket");
	        }

	        def onMessage(data: String) {
	        	println("Message received from WebSocket '"+data+"'");
	        }

	        def onDisconnect(closeCode: Int, msg: String) {
	        	println("Client disconnected from WebSocket");
	        }

	}

}
*/
