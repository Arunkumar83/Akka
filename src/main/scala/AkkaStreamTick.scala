import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, ClosedShape}
import akka.stream.scaladsl.{Flow, GraphDSL, Merge, RunnableGraph, Sink, Source, Zip}

import scala.concurrent.Future
import scala.concurrent.duration._

object AkkaStreamTick extends  App {

  implicit val actorSystem = ActorSystem()
  implicit val materializer = ActorMaterializer()(actorSystem)

  val in = Source.tick(1 second, 1 second,1)
  val doubleFlow = Flow[Int].map(_ * 2)
  val out = Sink.foreach(println)

  val naturalNumbers = Source(Stream.from(1))

  //in.via(doubleFlow).runWith(out)

  println("Future ...... ")
//  Source
//    .fromFuture(Future.successful(1 to 10)) // Source[Future[Int]]
//    .mapConcat(identity) // Source[Int]
//    .runForeach(println)

  println( "mapconcat ..... ")
  Source(1 to 10)
    .grouped(2) //Vector(1,2) Vector(3,4) .. vector(9,10)
    .mapConcat(identity)
    .runForeach(println)


  // prints 1 2 3 4 5 6 7 8 9 10

  /*println("merge flow ....")
  val mergeGraph = GraphDSL.create(){implicit b=>
    import GraphDSL.Implicits._

    val merge = b.add(Merge[Int](2))
    in ~> doubleFlow ~> merge ~> out
                  in ~> merge
    ClosedShape
  }*/

  //RunnableGraph.fromGraph(mergeGraph).run()

//  val zipGraph = GraphDSL.create(){implicit b =>
//
//    import GraphDSL.Implicits._
//
//    val zip = b.add(Zip[Int,Int])
//    naturalNumbers ~> zip.in0
//    in ~> zip.in1
//
//    zip.out ~> out
//
//    ClosedShape
//  }
//
//  RunnableGraph.fromGraph(zipGraph).run()

}
