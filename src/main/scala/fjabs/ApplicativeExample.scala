package fjabs

import cats._
import cats.implicits._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.util.Try

object ApplicativeExample extends App {

  val f1: Future[Int] = Applicative[Future].map2(Future{1}, Future{2}){_ + _}
  println(Await.result(f1, Duration.Inf))

  val f2: Future[Int] = (Future{1}, Future{2}).mapN(_ + _)
  println(Await.result(f2, Duration.Inf))

  val f3: Option[List[Int]] = List(Some(1), None, Some(3)).sequence
  println(f3)

  val f4: Option[List[Int]] = List(Option(1), Option(2), Option(3)).sequence
  println(f4)

  val f5: Try[List[Int]] = List(Try(1), Try{throw new IllegalArgumentException}, Try(3)).traverse(identity)
  println(f5)
}
