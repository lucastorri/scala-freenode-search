package co.torri.scalafns.library

import co.torri.scalafns.library

import org.apache.lucene.analysis._
import org.apache.lucene.document._
import org.apache.lucene.index._
import org.apache.lucene.store._
import scala.io.Source
import java.net.URL
import java.io.BufferedReader

import co.torri.scalafns.ScalaFNSTest

import org.specs2.matcher._
import org.specs2.execute._


class LogsLibraryTest extends ScalaFNSTest {
  
  "when adding a log to the library" should {
    "pass it to the lucene index" in new normalContext {
      log.contents returns Source.fromString(docContent)
      log.url returns new URL(docURL)
      factory.newWriter returns writer
      
      library.addLog(log)
      
      there was one (writer).addDocument(aDocumentWith(docURL, docContent))
    }
  }

  trait normalContext extends Context {
    val docURL = "http://resource.url"
    val docContent = "doc content"
    
    val log = mock[ChatLog]
    val writer = mock[IndexWriter]
    val factory = mock[IndexWriterFactory]
    val library = new ChatLogsLibrary(factory) 
  }
  
  case class DocumentMatcher(_url: String, _content: String) extends Matcher[Document] {
    
    private def _success[S <: Document](d: S) =
      MatchSuccess("Document matched", "", MustExpectable(d))
      
    private def _failure[S <: Document](d: S) =
      MatchFailure("", "Document not matched", MustExpectable(d), _failureDetails(d))
      
    private def _failureDetails(d: Document) =
      FailureDetails((_url, _content).toString, (_url(d), _content(d)).toString)
    
    private def _content(d: Document) = {
      val reader = d.getFieldable("contents").readerValue
      reader.reset
      new BufferedReader(reader).readLine
    }
    
    private def _url(d: Document) =
      d.get("url")
  
    def apply[S <: Document](e: Expectable[S]) =
      if (_url(e.value) == _url && _content(e.value) == _content) _success(e.value)
      else _failure(e.value)
  
  }

  def aDocumentWith(url: String, content: String) =
    DocumentMatcher(url, content)
  
}
