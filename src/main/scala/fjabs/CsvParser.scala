package fjabs

import cats._
import cats.implicits._

sealed trait CsvRow
case class Person(firstName: String, lastName: String) extends CsvRow
case class Address(street: String, city: String, country: String) extends CsvRow

trait CsvParser[A] {
  def parse(in: String): Either[Throwable, A]
}

object CsvParser{

  val personCsvParser: CsvParser[CsvRow] = in => Either.catchNonFatal{
    val fields = in.split(",")
    Person(fields(0), fields(1))
  }

  val addressCsvParser: CsvParser[CsvRow] = in => Either.catchNonFatal{
    val fields = in.split(",")
    Address(fields(0), fields(1), fields(2))
  }

  val listCsvParser: CsvParser[List[CsvRow]] = in => Either.catchNonFatal{
    val fields = in.split(",")
    List(Person(fields(0), ""), Person("", fields(1)))
  }

  val tupleCsvParser: CsvParser[(CsvRow,CsvRow)] = in => Either.catchNonFatal{
    val fields = in.split(",")
    (Person(fields(0), ""), Person("", fields(1)))
  }
}


object CsvParserApp extends App {

  import CsvParser._
  implicit val csvParserAlternative = new Alternative[CsvParser]{
    override def ap[A, B](ff: CsvParser[A => B])(fa: CsvParser[A]): CsvParser[B] = in => fa.parse(in).ap(ff.parse(in))
    override def empty[A]: CsvParser[A] = _ => Left(new Exception())
    override def pure[A](x: A): CsvParser[A] = _ => Right(x)
    override def combineK[A](x: CsvParser[A], y: CsvParser[A]): CsvParser[A] = in => x.parse(in).orElse(y.parse(in))
  }

  implicit val csvParserMonad = new Monad[CsvParser] {
    override def flatMap[A, B](fa: CsvParser[A])(f: A => CsvParser[B]): CsvParser[B] = in => fa.parse(in) match {
      case Right(a) => f(a).parse(in)
      case Left(t) => Left(t)
    }

    override def tailRecM[A, B](a: A)(f: A => CsvParser[Either[A, B]]): CsvParser[B] = in => f(a).parse(in) match {
      case Right(Right(b)) => Right(b)
      case Right(Left(nextA)) => tailRecM(nextA)(f).parse(in)
      case Left(t) => Left(t)
    }

    override def pure[A](x: A): CsvParser[A] = _ => Right(x)
  }

  val csvFile = List(
    "george,smith",
    "oak road,london,uk",
    "grove road,london,uk",
    "tom,taylor"
  )

  val rows: List[Either[Throwable, CsvRow]] = csvFile.map{ row =>
    (addressCsvParser <+> personCsvParser).parse(row)
  }

  println(rows)

  val rows2: List[Either[Throwable, List[CsvRow]]] = csvFile.map{ row =>
    listCsvParser.parse(row)
  }


  println(rows2)

  //val x = listCsvParser.unite.parse("george,smith")
  println(listCsvParser.unite.parse("george,smith"))
  println(listCsvParser.parse("george,smith"))

  println(tupleCsvParser.unite.parse("george,smith"))
  println(tupleCsvParser.separate._1.parse("george,smith"))
  println(tupleCsvParser.separate._2.parse("george,smith"))
  println(tupleCsvParser.parse("george,smith"))

  //val x = implicitly[Monad[CsvParser]]

}


