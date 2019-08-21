package fjabs

//A monoid represents types that can be summed
trait Monoid[A] {
  def combine(a1: A, a2: A): A
  def empty: A //'empty' is the identity for the combine operation
}

object Monoid {

  def apply[A: Monoid]: Monoid[A] = implicitly[Monoid[A]]

  implicit val intMonoid = new Monoid[Int] {
    override def combine(a1: Int, a2: Int): Int = a1 + a2
    override def empty: Int = 0
  }

  implicit val stringMonoid = new Monoid[String] {
    override def combine(a1: String, a2: String): String = a1 + a2
    override def empty: String = ""
  }

  val multiplicativeMonoid = new Monoid[Int] {
    override def combine(a1: Int, a2: Int): Int = a1 * a2
    override def empty: Int = 1
  }
}

object Sum extends App {

  //Sum over the elements of a list, as long as there is a Monoid for said elements
  def sum[A: Monoid](xs: List[A]): A = xs.foldLeft(Monoid[A].empty)(Monoid[A].combine)

  println(sum(List(1,2,3,4)))
  println(sum(List("1","2","3")))
  println(sum(List(1,2,3,4))(Monoid.multiplicativeMonoid))

}


