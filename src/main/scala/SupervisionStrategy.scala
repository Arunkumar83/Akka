import akka.actor.SupervisorStrategy.{Escalate, Restart, Resume, Stop}
import akka.actor.{Actor, ActorSystem, OneForOneStrategy, Props}

class Printer extends Actor {

  override def preRestart(reason: Throwable, message: Option[Any]): Unit = {
    println("Restarting due to ArithMeticException")
  }
  override def receive: Receive = {
    case msg:String => println(msg)
    case i:Int => 1 / 0
  }
}

class SupervisorActor extends  Actor {
  import scala.concurrent.duration._
  override  val  supervisorStrategy =
    OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 1.minute){
      case _:ArithmeticException => Stop
      case _:IllegalArgumentException => Stop
      case _:NullPointerException  => Resume
      case _:Exception => Escalate
    }
  val printer = context.actorOf(Props[Printer], "printer")

  override def receive: Receive = {
    case "Start" =>
      printer ! "hello printer"
      printer ! 10
      printer ! "Next hello"
  }
}
object SupervisionStrategy extends  App {

   val actorSystem = ActorSystem("SupervisionActorSystem")
  val supervisor = actorSystem.actorOf(Props[SupervisorActor], "SupervisorActor")
  supervisor ! "Start"
}
