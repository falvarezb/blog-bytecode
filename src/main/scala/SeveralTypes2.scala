
object SeveralTypes2 extends App {

  ////////////// COMPOUND TYPE
  //Trait linearisation
  trait A { def value = 10 }
  trait B extends A { override def value = super.value * 2 }
  trait C extends A { override def value = super.value + 2 }

  println(new A with B with C{}.value)
  println(new A with C with B{}.value)
}

/*
Notes:

Product types (cartesian product of sets) and sum/coproduct types (union of disjoint sets) together are known as algebraic data types. They are the foundation the data modeling in functional programming build upon.
They also allow us to use generic programming in Scala with libraries like shapeless.

Intersection types: a type belonging to several types
Currently, while A with B can be used in place of B with A and it just works.,
their behavior might be different due to change of order in trait linearization.
Given that intersection is supposed to be commutative, A with B is called compound type instead


 */


// F-Bound polymorphism: recursive type signatures
trait Doubler[T <: Doubler[T]] { self: T =>
  def double: T
}

case class Square(base: Double) extends Doubler[Square] {
  override def double: Square = Square(base * 2)
}

//WON'T COMPILE
//case class Apple(kind: String) extends Doubler[Square] {
//  override def double: Square = Square(5)
//}



