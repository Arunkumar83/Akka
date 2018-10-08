import scala.util.Try

import Model._
trait AppRepository[A,IDType] {
  def query(id: IDType): A
}

trait ApplicationRepositoryT extends  AppRepository[Application, Int]{
   def query(id: Int): Application
}

trait ApplicationRepository extends  ApplicationRepositoryT {
  override def query(id: Int): Application = DB.getApplicationById(id)
}

object ApplicationRepository extends  ApplicationRepository