import akka.actor.ActorSystem
import akka.stream._
import akka.stream.javadsl.RunnableGraph
import akka.stream.scaladsl.{Broadcast, Flow, GraphDSL, Sink, Source, Zip}

object AkkaStreamProduct extends App {

implicit  val actorSystem = ActorSystem("ProductSystem")
implicit val materializer = ActorMaterializer()(actorSystem)
  val stringToProducts = Flow[String].map(_.split(",")).map(toProductEntity)
  def toProductEntity(cols:Array[String]):MyProduct = new MyProduct(cols(0),cols(1))
  case class MyProduct(id:String, desc:String)
  val productlines = scala.io.Source.fromFile("d:\\test\\products.csv").getLines


  val g = RunnableGraph.fromGraph(GraphDSL.create(){ implicit builder =>

    import GraphDSL.Implicits._


    val readCSV:Outlet[String]  = builder.add(Source.fromIterator(() => productlines)).out
    val convertToProductEntity:FlowShape[String,MyProduct]  = builder.add(stringToProducts)
    val printProducts : Inlet[Any] = builder.add(Sink.foreach(println)).in
    builder.add(Broadcast[Int](2))

    readCSV ~> convertToProductEntity ~> printProducts

    ClosedShape
  })

   g.run(materializer)

  val MaximumDistinctWords = 7
  Source("Apple Orange Apple Guava Banana Orange Banana".split(" ").toList)
    // split the words into separate streams first
    .groupBy(MaximumDistinctWords, identity)
    //transform each element to pair with number of words in it
    .map(_ -> 1)
    // add counting logic to the streams
    .reduce((l, r) ⇒ (l._1, l._2 + r._2))
    // get a stream of word counts
    .mergeSubstreams
    .runForeach(println)


  val pairsGraph = GraphDSL.create(){ implicit b =>
    import GraphDSL.Implicits._

    val zip = b.add(Zip[Int, Int]())
    def ints = Source.fromIterator { () => Iterator.from(1) }
    ints.runForeach(println)
    ints.filter( _ % 2 != 0) ~> zip.in0
    ints.filter( _ % 2 == 0) ~> zip.in1

    SourceShape(zip.out)
  }

  val pairs = Source.fromGraph(pairsGraph)
  val firstPair = pairs.runWith(Sink.foreach { println _ })



  actorSystem.terminate()



}
