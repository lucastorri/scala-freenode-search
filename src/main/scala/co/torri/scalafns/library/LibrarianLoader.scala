package co.torri.scalafns.library

import javax.servlet._
import javax.servlet.http._
import akka.actor.Actor._
import java.io.File
import java.io.File.{ separator => | }
import org.apache.lucene.store._


class LibrarianLoader extends ServletContextListener {
  
  override def contextInitialized(e: ServletContextEvent): Unit = {
    val indexFolderPath = e.getServletContext.getResource(|) + "lucene"
    val factory = new IndexWriterFactory(FSDirectory.open(new File(indexFolderPath)))
    val library = new ChatLogsLibrary(factory)
    remote.start("localhost", 9123).register("librarian", actorOf(new Librarian(library)))
  }

  override def contextDestroyed(e: ServletContextEvent) {}

}