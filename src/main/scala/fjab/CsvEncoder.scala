package fjab

import shapeless.{::, Generic, HList, HNil}

/*
    How to derive type class instances for Product types.

    1. instances for HLists
    2. instances for any Product
 */

trait CsvEncoder[A] {

  def encode(a: A): List[String]
}

object CsvEncoder{

  def apply[A](implicit csvEncoder: CsvEncoder[A]): CsvEncoder[A] = csvEncoder
  def pure[A](func: A => List[String]): CsvEncoder[A] = a => func(a)

  implicit val stringEncoder: CsvEncoder[String] = pure(str => List(str))
  implicit val intEncoder: CsvEncoder[Int] = pure(num => List(num.toString))
  implicit val booleanEncoder: CsvEncoder[Boolean] = pure(bool => List(if(bool) "yes" else "no"))
  implicit val hnilEncoder: CsvEncoder[HNil] = pure(_ => Nil)

  implicit def hlistEncoder[H, T <: HList](implicit hEncoder: CsvEncoder[H], tEncoder: CsvEncoder[T]): CsvEncoder[H :: T] = pure {
    case h :: t => hEncoder.encode(h) ++ tEncoder.encode(t)
  }

//  implicit val iceCreamEncoder: CsvEncoder[IceCream] = {
//    val gen = Generic[IceCream]
//    val enc = CsvEncoder[gen.Repr]
//    pure(iceCream => enc.encode(gen.to(iceCream)))
//  }

  //Generic encoder supersedes iceCreamEncoder
  implicit def genericEncoder[A, R](
      implicit
        gen: Generic[A] {type Repr = R},
        enc: CsvEncoder[R]
      ): CsvEncoder[A] = pure(a => enc.encode(gen.to(a)))
}

object Main extends App{

  def writeCsv[A](values: List[A])(implicit enc: CsvEncoder[A]): String =
    values.map(value => enc.encode(value).mkString(",")).mkString("\n")

  println(writeCsv(List("casa" :: 1 :: true :: HNil)))
  println(writeCsv(List(IceCream("cornetto", 0, true))))
  println(writeCsv(List(("cornetto", 0, true))))
}
