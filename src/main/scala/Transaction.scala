
import OnlineService._
import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, ClosedShape}
import akka.stream.scaladsl.{Broadcast, Flow, GraphDSL, RunnableGraph, Sink, Source}

import scala.concurrent.Future
import java.util.{Calendar, Date}

import AkkaStreamGraphBroadcast.{bgraph, sink1, sink2}

import collection.mutable.{Map => MMap}
import scala.concurrent.ExecutionContext

object common{
  type Amount = BigDecimal
  val today = Calendar.getInstance().getTime
}


import common._

import scala.util.{Failure, Success, Try}

case class Balance(amount:Amount)
case class Account(no:String, startDate:Date = today, balance: Balance=Balance(0))

trait Repository[A,ID]{
  def query(id:ID):Try[Option[A]]
  def store(a:A):Try[A]
}

trait AccountRepository extends Repository[Account,String] {
  override def query(id: String): Try[Option[Account]]
  def store(a:Account):Try[Account]

  def balance(id:String):Try[Balance] = query(id) match {
    case Success(Some(a)) => Success(a.balance)
    case Success(None) => Failure(new Exception(s"Account does not exist $id"))
    case Failure(ex) => Failure(ex)
  }

  def query(openedDate:Date):Try[Seq[Account]]
}

trait AccountRespositoryInMM extends  AccountRepository {
  lazy val repo = MMap.empty[String,Account]
  def query(id:String):Try[Option[Account]] = Success(repo.get(id))
  def store(a:Account):Try[Account] = {
    repo += ((a.no,a))
    Success(a)
  }

  def query(openedDate:Date):Try[Seq[Account]] = Success(repo.values.filter(_.startDate == openedDate).toSeq)
}

sealed trait TransactionType
case object CREDIT extends  TransactionType
case object DEBIT extends TransactionType

import java.util.Date

import scala.concurrent.Future

object TransactionDomain {
  case class Tx(id:String, accountNo:String, debitCredit:TransactionType, amount:Amount, date:Date=today)
}

trait TxRepository{
  def query(no:String):Option[Account]
}

object TxRepository extends TxRepository {

  val m = Map("a-1" -> Account("a1",today),
    "a-2" -> Account("a2",today),
    "a-3" -> Account("a3",today))

  def query(no:String) = m.get(no)
}

import TransactionDomain._

trait OnlineService {
  def allAccounts(implicit ec:ExecutionContext) : Future[List[String]] = Future {
    List("a-1","a-2","a-3")
  }

  def queryAccount(no:String, repo:TxRepository) = repo.query(no).getOrElse(throw new Exception("Account not found"))

  val txns =
    List(
      Tx("t-1","a-1",DEBIT,1000),
      Tx("t-2","a-2",CREDIT,2000),
      Tx("t-3","a-3",DEBIT,1000)
    )

  def allTransactions(implicit ec: ExecutionContext):Future[List[Tx]]= Future{txns}
}

object OnlineService extends OnlineService

import OnlineService._

object TransactionObj extends App {

  implicit val actorSystem = ActorSystem("tx")
  implicit val ec = actorSystem.dispatcher
  implicit val materializer = ActorMaterializer()

  val transactions: Source[Tx, akka.NotUsed] = Source.fromFuture(allTransactions).mapConcat(identity)
  val accountNos: Source[String, akka.NotUsed] = Source.fromFuture(allAccounts).mapConcat(identity)

  val accounts = Flow[String].map(queryAccount(_, TxRepository))

  val txSink: Sink[Tx, Future[akka.Done]] = Sink.foreach(println)
  val accSink: Sink[Account, Future[akka.Done]] = Sink.foreach(println)

  transactions.to(txSink)

  val bgraph = GraphDSL.create() { implicit b =>

    import GraphDSL.Implicits._

   // val accountBroadcast = b.add(Broadcast[Int](2))

    accountNos ~> accounts ~> accSink

    ClosedShape
  }

  RunnableGraph.fromGraph(bgraph).run
}