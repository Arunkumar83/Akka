case class Product(name:String, price:Int)

object Learn extends App{

 val lineitems:Seq[Product] = Seq(Product("a",100),Product("b",200),Product("c",300))

  val price = lineitems.foldLeft(0)((acc,i) => acc + i.price)
   println(price)

  val li = List(1,2,3,5)

  println(li :+ 6)
}
