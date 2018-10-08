import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.{DefaultJsonProtocol, RootJsonFormat}



trait Protocols extends DefaultJsonProtocol with SprayJsonSupport {
  import Model._
  implicit val appCatFormat = jsonFormat2(ApplicationCategory)
  implicit val applicationFormat:RootJsonFormat[Application] = jsonFormat2(Application)
  implicit val appParams:RootJsonFormat[ApplicationParams] = jsonFormat10(ApplicationParams)
  implicit val appPost:RootJsonFormat[AppPost] = jsonFormat5(AppPost)
  implicit val appScore:RootJsonFormat[AppScore] = jsonFormat2(AppScore)

}

object Protocols extends Protocols
