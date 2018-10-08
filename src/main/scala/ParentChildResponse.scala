
import akka.actor.{Actor, ActorRef, ActorSystem, Props}

import scala.collection.mutable.ListBuffer

object Messages3{
  case object CreateChild1
  case class Double(x:Int)
  case class Response(x:Int)
  case object Send
}

import Messages3._

class DoubleActor extends  Actor {
  override def receive: Receive = {
    case Double(x) =>
      sender() ! Response(x * 2)
  }

}

class ParentActor1 extends Actor {
  var childs:ListBuffer[ActorRef] = scala.collection.mutable.ListBuffer[ActorRef]()

  override def receive: Receive = {
    case CreateChild1 =>
      childs += context.actorOf(Props[DoubleActor])
    case Send =>
      childs.zipWithIndex map {case (child, index) =>
          child ! Double(scala.util.Random.nextInt((10)))
      }

    case Response(x) => println(s"Received doubled response $x")
  }

}

object ParentChildResponse extends  App {

  val actorSystem = ActorSystem("ParentChildResponse")
  val parent1 = actorSystem.actorOf(Props[ParentActor1],"parent1")
  parent1 ! CreateChild1
  parent1 ! CreateChild1
  parent1 ! CreateChild1
  parent1 ! Send
}
