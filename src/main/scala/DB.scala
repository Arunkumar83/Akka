import doobie._
import doobie.implicits._
import cats._
import cats.effect._
import cats.implicits._
import spray.json._
import DefaultJsonProtocol._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport

import Model._
object DB  /*extends  App*/  {


    val xa = Transactor.fromDriverManager[IO](
      "org.h2.Driver","jdbc:h2:~/test","sa",
      ""
    )

  //import xa.yolo._
  def getApplicationById(id:Int): Application = {
    sql"select * from Application where appid=$id".query[Application].unique.transact(xa).unsafeRunSync
  }

  def getAllApplications():List[Application] ={
    sql"select * from Application".query[Application].to[List].transact(xa).unsafeRunSync
  }

  def update(id:Int, optionValue:String): Unit ={
    println(s"Updated ${id} - ${optionValue}")
  }

  def update1(appPost: AppPost): Unit ={
    println(s"Got ${appPost.id} - ${appPost.l1} - ${appPost.appparam} - ${appPost.option} - ${appPost.score}")
    isRecordExist(appPost.id) match {
      case Some(_) => updateRecord(appPost)
      case None => insertRecord(appPost)
      case _  => println(s"Record insertion failed for record ${appPost.id}")
    }
  }

  def insertRecord(appPost: AppPost): Unit = {
    sql"insert into applicationweightage values(${appPost.id},${appPost.l1},${appPost.appparam}, ${appPost.option}, ${appPost.score})".update.run.transact(xa).unsafeRunSync
  }

  def updateRecord(appPost:AppPost):Unit = {
    sql"update applicationweightage set app_selectedoption=${appPost.option} , score=${appPost.score} where id=${appPost.id}".update.run.transact(xa).unsafeRunSync
  }

  def isRecordExist(id:Int):Option[AppPost] = {
     sql"select * from applicationweightage where id = ${id}".query[AppPost].option.transact(xa).unsafeRunSync()
  }

  def getApplicationParams:List[ApplicationParams] ={
    sql"select app.id, app.level_1, app.level_2, app.level_3, param.param_title, param.description,param.option1,param.option2,param.option3,param.option4 from appgroup as app join appparam param on param.groupid = app.id group by app.level_1, app.level_2, app.level_3, param.param_title".query[ApplicationParams].to[List].transact(xa).unsafeRunSync
  }

  def getApplicaitonWeightage: List[AppPost] = {
    sql"select * from applicationweightage".query[AppPost].to[List].transact(xa).unsafeRunSync
  }

  def getAllScoresForApp:List[AppScore] = {
    sql"select app_level1, sum(score) from applicationweightage  group by app_level1".query[AppScore].to[List].transact(xa).unsafeRunSync
  }

  def getScoreByL1(level1:String):AppScore = {
    println("retrieving score for " + level1)
    val res = sql"select app_level1, sum(score) from applicationweightage  where app_level1= ${level1}".query[AppScore].unique.transact(xa).unsafeRunSync
    println(res)
    res
  }

  //println(getApplicationParams())
  //println(getApplicaitonWeightage())
  //println(getApplicationById(1234))
  //println(getScoreByL1("Application Maturity"))
//  case class Result(appid:Int, category:String, parameter:String)
//  object MyJsonProtocol extends DefaultJsonProtocol with SprayJsonSupport {
//    implicit val resultFormat = jsonFormat3(Result)
//  }
  //sql"select * from test".query[Test].to[List].transact(xa).unsafeRunSync.take(5).foreach(println)
//  val appid =1
//  val res = sql"select appcat.appid, c.name , p.parameter  from APPLICATION_CATEGORY as appcat JOIN APPLICATIONCATEGORY  c on c.catid= appcat.catid JOIN APPLICATIONPARAMETERS as p where appcat.appid=1 and appcat.catid= p.FK_APP_CAT"
//       .query[Result].to[List].transact(xa).unsafeRunSync
//  import MyJsonProtocol._
 // println(res.groupBy(_.appid).toJson)

  //println(res.toJson)

}

