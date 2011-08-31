package co.torri.scalafns.library

import co.torri.scalafns.library

import org.apache.lucene.analysis._
import org.apache.lucene.document._
import org.apache.lucene.index._
import org.apache.lucene.store._
import org.apache.lucene.analysis.standard._
import org.apache.lucene.util.Version._
import org.apache.lucene.queryParser._
import org.apache.lucene.search._
import scala.io.Source
import java.net.URI
import java.io.BufferedReader

import co.torri.scalafns.ScalaFNSTest

import org.specs2.matcher._
import org.specs2.execute._


class LogsLibraryTest extends ScalaFNSTest {
  
  "when using the log to the library" should {
    "add logs to the lucene index" in new normalContext {
      log.contents returns Source.fromString(docContent)
      log.uri returns new URI(docURI)
      factory.newWriter returns writer
      
      library.addLog(log)
      
      there was one (writer).addDocument(aDocumentWith(docURI, docContent))
    }
    
    "search the lucene index" in new normalContext {
      factory.indexExists returns true
      factory.newSearcher returns searcher
      searcher.search(any[Query], any[Int]) returns new TopDocs(1, Array(new ScoreDoc(123, 0)), 0)
      searcher.doc(123) returns doc

      library.searchLogs("query").chatLogs.head.uri must be equalTo(new URI(docURI))
    }
    
    "return an empty result if the index is not available" in new normalContext {
      factory.indexExists returns false
      
      library.searchLogs("query").totalHits must be equalTo(0)
    }
  }

  trait normalContext extends Context {
    val docURI = "/path/to/some/file"
    val docContent = "doc content"
    val doc = new Document
    doc add new Field("uri", docURI, Field.Store.YES, Field.Index.NOT_ANALYZED_NO_NORMS)
    
    val log = mock[ChatLog]
    val writer = mock[IndexWriter]
    val searcher = mock[IndexSearcher]
    val parser = mock[QueryParser]
    val factory = mock[IndexFactory]
    factory.newQueryParser(any[String]) returns parser
    val library = new ChatLogsLibrary(factory) 
  }
  
  case class DocumentMatcher(_uri: String, _content: String) extends Matcher[Document] {
    
    private def _success[S <: Document](d: S) =
      MatchSuccess("Document matched", "", MustExpectable(d))
      
    private def _failure[S <: Document](d: S) =
      MatchFailure("", "Document not matched. Expected was <" + _uri + "> and <" + _content + ">, but was <" + _uri(d) + "> and <" + _content(d) + ">", MustExpectable(d), _failureDetails(d))
      
    private def _failureDetails(d: Document) =
      FailureDetails((_uri, _content).toString, (_uri(d), _content(d)).toString)
    
    private def _content(d: Document) = {
      val reader = d.getFieldable("contents").readerValue
      reader.reset
      new BufferedReader(reader).readLine
    }
    
    private def _uri(d: Document) =
      d.get("uri")
  
    def apply[S <: Document](e: Expectable[S]) =
      if (_uri(e.value) == _uri && _content(e.value) == _content) _success(e.value)
      else _failure(e.value)
  
  }

  def aDocumentWith(uri: String, content: String) =
    DocumentMatcher(uri, content)
  
}
