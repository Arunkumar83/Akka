import akka.actor.{Actor, ActorSystem, Props}

import scala.concurrent.Await

class DoubleValueActor extends Actor {
  override def receive: Receive = {
    case num:Int => sender() ! num*2
  }
}

object AskPattern extends  App {
  import akka.pattern.ask
  import akka.util.Timeout
  import scala.concurrent.Await
  import scala.concurrent.duration._

  implicit  val timeout = Timeout(10 seconds)
  val actorSystem = ActorSystem("AskActorSystem")
  val actor = actorSystem.actorOf(Props[DoubleValueActor], "DoubleValActor")
  val f = (actor ? 10).mapTo[Int]
  val dval = Await.result(f, 2.seconds)
  println(dval)


}
