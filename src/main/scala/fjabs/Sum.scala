package fjabs

//A semigroup represents types that can be summed
trait Semigroup[A] {
  def combine(a1: A, a2: A): A
}

object Semigroup {
  //Summoner method
  def apply[A: Semigroup]: Semigroup[A] = implicitly[Semigroup[A]]

  //Constructor
  def instance[A](f: (A, A) => A): Semigroup[A] = (a1: A, a2: A) => f(a1, a2)

  //Type class instances
  implicit val intSemigroup = new Semigroup[Int] {
    override def combine(a1: Int, a2: Int): Int = a1 + a2
  }

  //implicit val intSemigroup2: Semigroup[Int] = instance((a1,a2) => a1+a2)

  implicit val stringSemigroup = new Semigroup[String] {
    override def combine(a1: String, a2: String): String = a1 + a2
  }

  implicit val boolAndSemigroup = new Semigroup[Boolean] {
    override def combine(a1: Boolean, a2: Boolean): Boolean = a1 && a2
  }

  implicit def setUnionSemigroup[A] = new Semigroup[Set[A]] {
    override def combine(a1: Set[A], a2: Set[A]): Set[A] = a1 union a2
  }
}

//A monoid is a semigroup with an empty value
trait Monoid[A] extends Semigroup[A] {
  def empty: A //'empty' is the identity for the combine operation
}
final case class Pair[A, B](first: A, second: B)

object Monoid {

  import Semigroup._
  //Summoner method
  //def apply[A: Monoid]: Monoid[A] = implicitly[Monoid[A]]
  def apply[A](implicit ev: Monoid[A]): Monoid[A] = ev

  implicit def intMonoid: Monoid[Int] = new Monoid[Int] {
    override def combine(a1: Int, a2: Int): Int = intSemigroup.combine(a1, a2) //implicit derivation results in an infinitely recursive call
    override def empty: Int = 0
  }

  implicit def stringMonoid: Monoid[String] = new Monoid[String] {
    override def combine(a1: String, a2: String): String = stringSemigroup.combine(a1, a2)
    override def empty: String = ""
  }

  implicit def boolAndMonoid: Monoid[Boolean] = new Monoid[Boolean] {
    override def empty: Boolean = true
    override def combine(a1: Boolean, a2: Boolean): Boolean = boolAndSemigroup.combine(a1, a2)
  }

  implicit def setUnionMonoid[A]: Monoid[Set[A]] = new Monoid[Set[A]] {
    override def empty: Set[A] = Set.empty
    override def combine(a1: Set[A], a2: Set[A]): Set[A] = setUnionSemigroup.combine(a1, a2)
  }

//  implicit def multiplicativeMonoid: Monoid[Int] = new Monoid[Int] {
//    override def combine(a1: Int, a2: Int): Int = a1 * a2
//    override def empty: Int = 1
//  }

  //====== TYPECLASS DERIVATION ======

  //for any Semigroup[A], there is a Monoid[Option[A]]
  implicit def optionInstance[A: Semigroup]: Monoid[Option[A]] = new Monoid[Option[A]] {
    override def empty: Option[A] = None
    override def combine(a1: Option[A], a2: Option[A]): Option[A] = a1 match {
      case Some(value1) => a2 match {
        case Some(value2) => Some(Semigroup[A].combine(value1, value2))
        case None => a1
      }
      case None => a2
    }
  }

  //derivation of Monoid[Pair[A,B]] from Monoid[A] and Monoid[B]
  implicit def tupleToPairInstance[A: Monoid, B: Monoid]: Monoid[Pair[A,B]] = new Monoid[Pair[A,B]] {
    override def combine(a: Pair[A, B], b: Pair[A, B]): Pair[A, B] = Pair(Monoid[A].combine(a.first, b.first), Monoid[B].combine(a.second, b.second))
    override def empty: Pair[A, B] = Pair(Monoid[A].empty, Monoid[B].empty)
  }
}

object Sum extends App {

  //Sum over the elements of a list, as long as there is a Monoid for said elements
  def sum[A: Monoid](xs: List[A]): A = xs.foldLeft(Monoid[A].empty)(Monoid[A].combine)

  println(sum(List(1,2,3,4)))
  println(sum(List("1","2","3")))
  //println(sum(List(1,2,3,4))(Monoid.multiplicativeMonoid))
  println(sum(List(Pair(1,"2"), Pair(3,"4"))))
  println(sum(List(Option(2), Option(3), None)))
  println(sum(List(Option(true), Option(true), None)))
  println(Monoid[Set[Int]].combine(Set(1,2), Set(2,3)))
  println(sum(List(Option(Set(1,2)), Option(Set(2,3)), None)))

}


