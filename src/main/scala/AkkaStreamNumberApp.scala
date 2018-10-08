import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, ThrottleMode}
import akka.stream.scaladsl.{Flow, Sink, Source}

import scala.concurrent.Future
import scala.concurrent.duration._

object AkkaStreamNumberApp extends App {

  implicit val actorSystem = ActorSystem("NumberSystem")
  implicit val materializer = ActorMaterializer()
  val numberSource = Source(1 to 10)
  val numberSink = Sink.foreach(println)
  val numberFlow = Flow[Int].map(x => x +1)
  val runnableGraph = numberSource.via(numberFlow).to(numberSink)
  //runnableGraph.run()

  //Source(1 to 100).map(_ * 2 ).groupedWithin(5,10.seconds).runForeach(println)
import  scala.concurrent.ExecutionContext.Implicits._
  def writeBatchToDatabase(batch: Seq[Int]): Future[Unit] =
    Future {
      println(s"Writing batch of $batch to database by ${Thread.currentThread().getName}")
    }

  Source(1 to 1000)
    .grouped(10)
    .throttle(elements = 10, per = 1.second, maximumBurst = 10, ThrottleMode.shaping)
    .mapAsync(5)(writeBatchToDatabase)
    .runWith(Sink.ignore)


}
