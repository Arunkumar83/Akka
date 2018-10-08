import Model._
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshalling.{ToResponseMarshallable, ToResponseMarshaller}
import akka.http.scaladsl.model.ws.{Message, TextMessage}
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Flow

import scala.io.StdIn

object Webserver extends App with Protocols with CorsSupport {
  //def main(args: Array[String]) {
 import Model._
    implicit val system = ActorSystem("my-system")
    implicit val materializer = ActorMaterializer()
    // needed for the future flatMap/onComplete in the end
    implicit val executionContext = system.dispatcher



    val route =
      path("hello") {
        get {
          complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>Say hello to akka-http</h1>"))
        }
      }~
        pathPrefix("app"){
          path(Segment){id =>
            get {
              complete(DB.getApplicationById(id.toInt))
            }
          }~
          get{
            complete(DB.getApplicationParams)
          }~
            (post & parameters('id,'option)){(id,option) =>
              println("In post method...")
              DB.update(id.toInt,option)
              complete("done")
            }
       }~
        post{
          //path("query") {
            entity(as[AppPost]) { app =>
              println("IN post method 2")
              DB.update1(app)
              complete(StatusCodes.OK)
            }
         // }
        }~
        pathPrefix("progress"){
          path(Segment) { l1 =>
            get {
              println(l1.toString)
              complete(DB.getScoreByL1(l1))
            }
          }~
          get{
            complete(DB.getAllScoresForApp)
          }
      }

  val websocketRoute =
    path("greeter") {
      handleWebSocketMessages(echoService)
    }

  val echoService: Flow[Message, Message, _] = Flow[Message].map {
    case TextMessage.Strict(txt) => TextMessage("ECHO: " + DB.getApplicationParams)
    case _ => TextMessage("Message type unsupported")
  }
            //}
    val bindingFuture = Http().bindAndHandle(corsHandler(route ~ websocketRoute), "localhost", 9090)

    println(s"Server online at http://localhost:9090/\nPress RETURN to stop...")
    StdIn.readLine() // let it run until user presses return
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => system.terminate()) // and shutdown when done
  //}

  /*~
        path("randomitem") {
          get {
            // will marshal Item to JSON

            val technologyCat = ApplicationCategory("Technology", List("Cost of App. dev", "Alignment with arch standards", "Quality", "Complexity"))
            val peopleOrg = ApplicationCategory("People And Organisation", List("Business Cost","Business Objective"))
            val warehouseMgmtApp:Application = Application(1234, "WarehouseManagement", "Pascal", List(technologyCat,peopleOrg))

            complete(ToResponseMarshallable(warehouseMgmtApp))
          }
        }*/
}
