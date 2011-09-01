package co.torri.scalafns.library

import org.apache.lucene.analysis._
import org.apache.lucene.analysis.standard._
import org.apache.lucene.document._
import org.apache.lucene.index._
import org.apache.lucene.store._
import org.apache.lucene.util.Version._
import org.apache.lucene.queryParser._
import org.apache.lucene.search._
import java.net.URI
import scala.io.Source
import scala.io.Source._
import java.io.StringReader
import org.apache.lucene.index.IndexWriterConfig.OpenMode._
import grizzled.slf4j._


class ChatLogsLibrary(_factory: IndexFactory) extends Logging {
  
  private val _searchLimit = 20
  private val _identifierField = "uri"
  private val _contentField = "contents"
  private val _queryParser = _factory.newQueryParser(_contentField)

  def addLog(log: ChatLog) = try {
    
    val writer = _factory.newWriter
    writer.addDocument(log)
    writer.close
    
  } catch { case e => logger.error("Error when indexing", e) }

  def searchLogs(query: String): ChatLogSearchResult = try {
    
    if (!_factory.indexExists) {
      logger.warn("Index doesn't exist")
      return ChatLogSearchResult(query)
    }
    val searcher = _factory.newSearcher
    val results = searcher.search(_queryParser.parse(query), _searchLimit)
    val docs = results.scoreDocs.map(_.doc).distinct.map(docId => _document2ChatLog(searcher.doc(docId)))
    searcher.close

    ChatLogSearchResult(query, docs, results.totalHits)
    
  } catch { case e => logger.error("Error when searching for " + query, e); ChatLogSearchResult(query) }
  
  private implicit def _document2ChatLog(d: Document): ChatLog =
    ChatLog(new URI(d.get(_identifierField)))
    
  private implicit def _chatLog2Document(log: ChatLog): Document = {
    val doc = new Document
    doc add new Field(_identifierField, log.uri.getPath, Field.Store.YES, Field.Index.NOT_ANALYZED_NO_NORMS)
    doc add new Field(_contentField, new StringReader(log.contents.mkString))
    doc
  }

}

class IndexFactory(_indexPath: Directory) {
  
  private val _version = LUCENE_31
  
  private def _analyzer =
    new StandardAnalyzer(_version)

  private def _config =
    new IndexWriterConfig(_version, _analyzer).setOpenMode(CREATE_OR_APPEND)
  
  def newWriter =
    new IndexWriter(_indexPath, _config)
    
  def newSearcher =
    new IndexSearcher(_indexPath)

  def newQueryParser(fieldName: String) =
    new QueryParser(_version, fieldName, _analyzer)
  
  def indexExists =
    IndexReader.indexExists(_indexPath)
    
}

case class ChatLog(val uri: URI) {

  def contents: Source =
    fromFile(uri.getPath)

}

case class ChatLogSearchResult(query: String, chatLogs: Seq[ChatLog], totalHits: Int)

object ChatLogSearchResult {
  
  def apply(query: String): ChatLogSearchResult =
    apply(query, Seq[ChatLog](), 0)
  
}
