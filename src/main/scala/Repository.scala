import java.net.HttpURLConnection
import java.util.{Calendar, Date}

import scala.util.{Failure, Success, Try}
import collection.mutable.{Map => MMap}



trait Numeric[A]{
  def plus(x:A,y:A):A
  def zero : A
}

trait Addable[A]{
  def plus(x:A,y:A):A
}

trait AddableWithZero[A] extends Addable[A]{
  def zero : A
}

object Addable {
  implicit def numericAdd:AddableWithZero[Int] = new AddableWithZero[Int] {
    override def plus(x: Int, y: Int): Int = x + y
    override  def zero  = 0
}

  implicit def stringAdd:AddableWithZero[String] = new AddableWithZero[String] {
    override def plus(x: String, y: String): String = x + y
    override def zero:String=""
  }

  def sumGeneric1[A](l:List[A])(implicit A: Addable[A]) = l.reduce(A.plus)

  def sumGeneric[A](l:List[A])(implicit A: AddableWithZero[A]) = l.foldLeft(A.zero)(A.plus)
}

object Main extends App {
  type HttpOption = HttpURLConnection => Unit

  def connectionTimeOut(timeout:Int):HttpOption = c => c.setConnectTimeout(timeout)
  def readTimeOut(timeout:Int):HttpOption = c => c.setReadTimeout(timeout)

  val map = Map(1 -> List("one"), 2 -> List("two"), 3 -> List("three"))
  println(map.get(1).flatMap(_.headOption).get)
}