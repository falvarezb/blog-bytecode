package fjabs

import cats._
import cats.implicits._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.util.Try

case class Example(a: String)

object ApplicativeExample extends App {

  val f1: Future[String] = Applicative[Future].map2(Future{"ff"}, Future{Example("3")}){_ + _}
  println(Await.result(f1, Duration.Inf))

  val f2: Future[Int] = (Future{1}, Future{2}).mapN(_ + _)
  println(Await.result(f2, Duration.Inf))

  val f3: Option[List[Int]] = List(Some(1), None, Some(3)).sequence
  println(f3)

  val f4: Option[List[Int]] = List(Option(1), Option(2), Option(3)).sequence
  println(f4)

  val f5: Try[List[Int]] = List(Try(1), Try{throw new IllegalArgumentException}, Try(3)).traverse(identity)
  println(f5)

  val f6 = (Option(1),none[Int]).mapN(_ + _)
  println(f6)

  /*
    what is ap?
    We want a function that takes Option[A], Option[B] and f: (A,B) => C and returns Option[C]
  */
  val f: (Int, Char) => Double = (i, c) => (i + c).toDouble
  val int: Option[Int] = Some(5)
  val char: Option[Char] = Some('a')

  val apresult = Applicative[Option].ap2(Applicative[Option].pure(f))(int, char)
  println(apresult)

  val apresult2 = f.pure[Option] ap2 (int, char)
  println(apresult2)
}
