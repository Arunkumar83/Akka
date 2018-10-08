import akka.actor.{Actor, ActorRef, ActorSystem, FSM, Props}

object FSMOrderState{
  sealed trait OrderState
  case object SUBMITTED extends  OrderState
  case object VALIDATE extends  OrderState
  case object ACCEPTED extends OrderState
  case object PICK extends OrderState
  case object PACK extends OrderState
  case object READY extends OrderState
  case object SHIPPED extends OrderState
  case object REJECTED extends  OrderState
}

object  FSMOrderData {
  sealed trait OrderData
  case object NoOrder extends OrderData
  case class HandlingOrder(orderId:String) extends  OrderData
  case class ValidateOrder(orderid:String) extends OrderData
  case class ReadyForPickup(orderid:String) extends  OrderData
  case class AcceptedOrder(orderid:String) extends  OrderData
  case class RejectedOrder(orderid:String) extends  OrderData
  case class OrderPicked(orderid:String) extends OrderData
  case class OrderStatus(status:String)
}

import FSMOrderState._
import FSMOrderData._

class FSMOrderActor(actorRef:ActorRef) extends Actor with FSM [OrderState,OrderData]{

  val validateOrderActor = context.actorOf(Props[ValidateOrderActor],"validateOrderActor")
  val pickupOrderActor = context.actorOf(Props[PickOrderActor],"pickupOrderActor")

  startWith(SUBMITTED, NoOrder)

  when(SUBMITTED){
    case Event(HandlingOrder(orderid),_) =>
      actorRef ! OrderStatus("OrderSubmitted")
      Thread.sleep(1000)
      goto(VALIDATE) using HandlingOrder(orderid)
  }

  when(VALIDATE){

    case Event(AcceptedOrder(orderid),_) =>
      goto(PICK) using AcceptedOrder(orderid)
    case Event(RejectedOrder(orderid), _ ) =>
      goto(REJECTED)
  }

  when(ACCEPTED){
    case Event(AcceptedOrder(id),_) =>
      actorRef ! OrderStatus("OrderAccepted")
      //pickupOrderActor ! AcceptedOrder(id)
      goto(PICK) using AcceptedOrder(id)

    //handle pickup rejected
  }

  when(REJECTED){
    case Event(RejectedOrder(id),_) =>
      actorRef ! OrderStatus("Rejected")
      goto(SUBMITTED)
  }

  when(PICK){
    case Event(AcceptedOrder(id),_) =>

      stay()
    case Event(OrderPicked(id),_) =>
      actorRef ! OrderStatus("OrderPicked")
      stay()
  }

  onTransition{
    case SUBMITTED -> VALIDATE =>
      //println("log: Order status changed to validate state" + stateData + "  ===> " + nextStateData)
      validateOrderActor ! nextStateData
    case VALIDATE -> ACCEPTED => //println("log: Order status : Accepted")
      println(stateData + " "+ stateName)
    case VALIDATE -> PICK => //println("log: Order status : Accepted")
      pickupOrderActor ! nextStateData
    case VALIDATE -> REJECTED => println("log: Order status : Rejected")
    case ACCEPTED -> PICK => println("Order status changed to PICK state")
      println(stateData + " "+ stateName)

  }

}

class ValidateOrderActor extends  Actor {

  override def receive: Receive = {
    case HandlingOrder(id) =>
      if(id.equals("1234")) {
        println("Order accepted and approved ")
        sender() ! AcceptedOrder(id)
      }else{

        sender() ! RejectedOrder(id)
      }
  }
}

class PickOrderActor extends Actor {
  override def receive: Receive = {
    case AcceptedOrder(id) => println("Picking up the order ")
      sender() ! OrderPicked(id)
  }
}

class StatusChangeActor extends Actor {
  override def receive: Receive = {
    case OrderStatus(status) => println(status)
  }
}

object FSMOrder  extends  App {
  val actorSystem = ActorSystem("OrderProcessing")
  val statusactor = actorSystem.actorOf(Props[StatusChangeActor], "StatusChangeActor")
  val actor = actorSystem.actorOf(Props(new FSMOrderActor(statusactor)), "FSMOrderActor")
  actor ! HandlingOrder("1234")
  //actor ! AcceptedOrder("1234")
}
