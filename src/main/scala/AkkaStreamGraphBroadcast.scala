import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, ClosedShape}
import akka.stream.scaladsl.{Broadcast, Flow, GraphDSL, Merge, RunnableGraph, Sink, Source}

object AkkaStreamGraphBroadcast extends App {


  implicit val actorSystem = ActorSystem()
  implicit val materializer = ActorMaterializer()(actorSystem)

  val sink1 = Sink.foreach[Int](n => println(s"Sink[1] => $n"))
  val sink2 = Sink.foreach[Int](n => println(s"Sink[2] => $n"))

  val bgraph = GraphDSL.create(sink1,sink2)((_,_)){implicit  b =>
    (s1,s2) =>
    import GraphDSL.Implicits._
    val source = Source(1 to 10)
    val bcast = b.add(Broadcast[Int](2))
      //TODO
    val merge = b.add(Merge[Int](2))

    val addOne = Flow[Int].map( _ + 1)
    val minusOne = Flow[Int].map( _  - 1)

    source ~> bcast.in

    bcast.out(0) ~> addOne ~> s1.in
    bcast.out(1) ~> minusOne ~> s2.in

    ClosedShape
  }

  RunnableGraph.fromGraph(bgraph).run
}
