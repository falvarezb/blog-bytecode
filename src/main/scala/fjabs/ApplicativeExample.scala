package fjabs

import cats._
import cats.implicits._

import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.util.{Failure, Try}

object ApplicativeExample extends App {

  val f: Future[Int] = Applicative[Future].map2(Future{1}, Future{2}){_ + _}
  println(Await.result(f, Duration.Inf))

  val g: Future[Int] = (Future{1}, Future{2}).mapN(_ + _)
  println(Await.result(g, Duration.Inf))

  val l: Option[List[Int]] = List(Some(1), None, Some(3)).sequence
  println(l)

  val t: Try[List[Int]] = List(Try(1), Try{throw new IllegalArgumentException}, Try(3)).traverse(identity)
  println(t)
}
