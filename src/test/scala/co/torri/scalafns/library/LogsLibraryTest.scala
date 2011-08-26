package co.torri.scalafns.library

import co.torri.scalafns.library

import org.apache.lucene.analysis._
import org.apache.lucene.document._
import org.apache.lucene.index._
import org.apache.lucene.store._
import scala.io.Source
import java.net.URI
import java.io.BufferedReader

import co.torri.scalafns.ScalaFNSTest

import org.specs2.matcher._
import org.specs2.execute._


class LogsLibraryTest extends ScalaFNSTest {
  
  "when adding a log to the library" should {
    "pass it to the lucene index" in new normalContext {
      log.contents returns Source.fromString(docContent)
      log.uri returns new URI(docURI)
      factory.newWriter returns writer
      
      library.addLog(log)
      
      there was one (writer).addDocument(aDocumentWith(docURI, docContent))
    }
  }

  trait normalContext extends Context {
    val docURI = "http://resource.uri"
    val docContent = "doc content"
    
    val log = mock[ChatLog]
    val writer = mock[IndexWriter]
    val factory = mock[IndexWriterFactory]
    val library = new ChatLogsLibrary(factory) 
  }
  
  case class DocumentMatcher(_uri: String, _content: String) extends Matcher[Document] {
    
    private def _success[S <: Document](d: S) =
      MatchSuccess("Document matched", "", MustExpectable(d))
      
    private def _failure[S <: Document](d: S) =
      MatchFailure("", "Document not matched", MustExpectable(d), _failureDetails(d))
      
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
