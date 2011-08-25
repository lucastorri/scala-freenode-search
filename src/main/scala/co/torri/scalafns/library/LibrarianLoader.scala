package co.torri.scalafns.library

import javax.servlet._
import javax.servlet.http._
import akka.actor.Actor._
import java.io.File
import java.io.File.{ pathSeparator => | }
import org.apache.lucene.store._


class WebSocketLoader extends ServletContextListener {
  
  override def contextInitialized(e: ServletContextEvent): Unit = {
    val indexFolderPath = e.getServletContext.getContextPath + | + "lucene"
    val factory = new IndexWriterFactory(FSDirectory.open(new File(indexFolderPath)))
    val library = new ChatLogsLibrary(factory)
    val librarian = new Librarian(library)
    actorOf(librarian).start
  }

  override def contextDestroyed(e: ServletContextEvent) {}

}