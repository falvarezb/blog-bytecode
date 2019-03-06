package fjabs

//A monoid represents types that can be summed
trait Monoid[A] {
  def mappend(a1: A, a2: A): A
  def mzero: A
}

object Monoid {

  implicit val intMonoid = new Monoid[Int] {
    override def mappend(a1: Int, a2: Int): Int = a1 + a2
    override def mzero: Int = 0
  }

  implicit val stringMonoid = new Monoid[String] {
    override def mappend(a1: String, a2: String): String = a1 + a2
    override def mzero: String = ""
  }

  val multiplicativeMonoid = new Monoid[Int] {
    override def mappend(a1: Int, a2: Int): Int = a1 * a2
    override def mzero: Int = 1
  }
}

object Sum extends App {

  //Sum over the elements of a list, as long as there is a Monoid for said elements
  def sum[A](xs: List[A])(implicit m: Monoid[A]): A = xs.foldLeft(m.mzero)(m.mappend)

  println(sum(List(1,2,3,4)))
  println(sum(List("1","2","3")))
  println(sum(List(1,2,3,4))(Monoid.multiplicativeMonoid))

}


