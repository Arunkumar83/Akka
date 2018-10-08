import akka.actor.{Actor, ActorRef, ActorSystem, Props}

import scala.util.Random

object Messages {
  case object GiveMeRandomNumber
  case class Start(actorRef: ActorRef)
  case class Done(randomNumnber: Int)
}

import Messages._
class RandomNumnberGenerator extends Actor {
  override def receive: Receive = {
    case GiveMeRandomNumber =>
      println("Received random num gen message")
      val  randomNum = Random.nextInt
      sender() ! Done(randomNum)
  }
}
class QueryActor extends Actor {
  override def receive: Receive = {
    case Start(actorRef) =>
      actorRef ! GiveMeRandomNumber
    case Done(randomNumnber) => println(s"received random num $randomNumnber" )
  }
}

object RandomNumnber extends  App {
  val actorSystem = ActorSystem("Random")
  val randomeNumGen = actorSystem.actorOf(Props[RandomNumnberGenerator], "RandonNumGenrator")
  val queryActor = actorSystem.actorOf(Props[QueryActor], "QueryActor")
  queryActor ! Start(randomeNumGen)
}
