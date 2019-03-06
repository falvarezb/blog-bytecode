package fjab

import shapeless.Generic.Aux
import shapeless._

object Intro extends App {

  def product() = {

    val product: String :: Int :: Boolean :: HNil = "Sunday" :: 1 :: false :: HNil

    println(product.head)
    println(product.tail)


    val iceCreamGen: Aux[IceCream, String :: Int :: Boolean :: HNil] = Generic[IceCream]
    val iceCream = IceCream("Sundae", 1, false)

    val iceCreamRepr = iceCreamGen.to(iceCream)
    val iceCream2 = iceCreamGen.from(iceCreamRepr)

    println(iceCreamRepr)
    println(iceCream2)
    println(iceCream == iceCream2) //true


    val employee = Generic[Employee].from(Generic[IceCream].to(iceCream))
    println(employee)
    println(iceCream == employee) //false
  }

  def coproduct() = {

    type Light = Red :+: Amber :+: Green :+: CNil

    val red: Light = Inl(Red())
    val green: Light = Inr(Inr(Inl(Green())))

    println(red)
    println(green)


    val gen = Generic[Shape]
    println(gen)

    val rectangleRepr = gen.to(Rectangle(3.0, 4.0))
    val circleRepr = gen.to(Circle(1.0))

    println(rectangleRepr)
    println(circleRepr)

    println(gen.from(rectangleRepr))
  }

  //product()
  coproduct()

}

case class IceCream(name: String, numCherries: Int, inCone: Boolean)
case class Employee(name: String, number: Int, manager: Boolean)
case class Red()
case class Amber()
case class Green()

sealed trait Shape
final case class Circle(radius: Double) extends Shape
final case class Rectangle(width: Double, height: Double) extends Shape