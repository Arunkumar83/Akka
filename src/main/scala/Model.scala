object Model {

  case class Application(id: Int, name: String /*techstack: String, categories: List[ApplicationCategory]*/)

  case class ApplicationCategory(name: String, parameters: List[String])

  case class ApplicationParams(id:Int, l1:String, l2:String,l3:String,param_title:String,desc:String,option1:String,option2:String,option3:String,option4:String)

  case class AppPost(id:Int,l1:String, appparam:String, option:String,score:Int)

  case class AppScore(l1:String, score:Int)
}
