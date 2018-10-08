
import akka.actor.{Actor, ActorSystem, Props}

object Messages1 {
  case class Greet(msg:String)
  case object CreateChildActor
}

import Messages1._

class ParentActor extends Actor {
  override def receive: Receive = {
    case CreateChildActor =>

      val child = context.actorOf(Props[ChildActor],"Child")
      child ! Greet("hi")
  }
}
class ChildActor extends Actor {
  override def receive: Receive = {
    case Greet(msg) =>
      println(s"${self.path.parent} ${self.path}")
      println(msg)
  }
}

object ParentChild extends App {

  val actorSystem = ActorSystem("ParentChildActor")
  val parent = actorSystem.actorOf(Props[ParentActor], "parent")
  parent ! CreateChildActor

}
