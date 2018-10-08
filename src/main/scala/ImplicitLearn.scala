
sealed trait Json

case class JsString(get:String) extends Json
case class JsNumber(get:Int) extends  Json

trait JsonWriter[A]{
  def write(value:A) : Json
}

object DefaultJsonWriters {
  implicit val stringJsonSwriter = new JsonWriter[String] {
    override def write(value: String): Json = JsString(value)
  }
}

object  Json {
  def toJson[A](value:A)(implicit  writer:JsonWriter[A]):Json =
    writer.write(value)
}

object JsonSyntax {
  implicit class JsonWriterOps[A](value:A) {
    def toJson(implicit  writer:JsonWriter[A]):Json = writer.write(value)
  }
}

case class Message(id:Int, author:String, body:String)

import java.util.UUID

import DefaultJsonWriters._
import JsonSyntax._

import scala.collection.SortedSet
import scala.collection.immutable.SortedMap
object ImplicitLearn extends  App {


  println("arun".toJson)
  println(Seq(1 -> "arun", 2 -> "sreedhar").unzip)
  val l = Seq(1,2,3,4,5)
  println(l.reduce(_ * _))
  println(l.foldLeft(0){_ + _})
  println(l.sum)

  val messages = List(
    Message(1, "arun", "welcome to scala"),
    Message(2, "arun", "welcome to java"),
    Message(4, "arun", "welcome to c++"),
    Message(3, "martin", "Scala for impatient")
  )

   def findByName(m:List[Message], author:String): List[Message] ={
     m.foldLeft(List[Message]()){(ms,m) =>
       if(m.author == author) ms:+m
       else ms
     }
   }

  def findByName1(m:List[Message],author:String): List[Message] = {
    m.filter(_.author == author)
  }

  def findByName3(messages: List[Message], name: String): List[Message] = {
    messages.aggregate(List[Message]())(
      (ms, m) => {
        if (m.author == name) m +: ms
        else ms
      },
      (a, b) => a ++ b
    )
  }

  println(findByName(messages, "arun"))
  println(findByName1(messages, "arun"))
  println(findByName3(messages, "arun"))

   val line = "This is is a a word count program and this uses scala"

  val wordCount  = line.split("\\W+").map(_.toLowerCase)
    .foldLeft(SortedMap[String,Int]()){
      (words,word) =>
      val count = words.getOrElse(word,0) + 1
      words + (word -> count)
    }

  println(wordCount)

  val numberedMsg = (1 to messages.length).map(_ => UUID.randomUUID()).zip(messages)
  println(numberedMsg)
  println(messages.groupBy(_.author))
}
