package co.torri.scalafns.library

import org.apache.lucene.analysis._
import org.apache.lucene.analysis.standard._
import org.apache.lucene.document._
import org.apache.lucene.index._
import org.apache.lucene.store._
import org.apache.lucene.util.Version._
import java.net.URI
import scala.io.Source
import scala.io.Source._
import java.io.StringReader
import org.apache.lucene.index.IndexWriterConfig.OpenMode._


class ChatLogsLibrary(_factory: IndexFactory) {

  private def _transaction(f: (IndexWriter) => Unit) = {
    val writer = _factory.newWriter
    f(writer)
    writer.close
  }

  def addLog(log: ChatLog) =
    _transaction(_.addDocument(log))
    
  private implicit def _chatLog2Document(log: ChatLog): Document = {
    val doc = new Document
    doc add new Field("uri", log.uri.toString, Field.Store.YES, Field.Index.NOT_ANALYZED_NO_NORMS)
    doc add new Field("contents", new StringReader(log.contents.mkString))
    doc
  }

}

class IndexFactory(_indexPath: Directory) {
  
  private val _version = LUCENE_31
  private def _config = new IndexWriterConfig(_version, new StandardAnalyzer(_version)).setOpenMode(CREATE_OR_APPEND)
  
  def newWriter =
    new IndexWriter(_indexPath, _config)
    
}

case class ChatLog(val uri: URI) {

  def contents: Source =
    fromURI(uri)

}
