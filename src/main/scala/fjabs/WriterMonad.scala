package fjabs

import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import cats._
import cats.data.Writer
import cats.implicits._

import scala.annotation.tailrec
import scala.util.Random

object WriterMonad extends App {

  def slowly[A](action: => A): A = {
    Thread.sleep(100)
    action
  }

  def factorial(n: Int): Int = {
    val result = slowly {
      if(n == 0) 1 else n*factorial(n-1)
    }
    println(s"${n}! = ${result}")
    result
  }

  case class Result(log: List[String], value: Int)
  def factorialTailRec(n: Int): Int = {
    def aux(m: Int, acc: Result): Result = {
      if(m == 0) Result(s"${n-m}! = ${acc.value}" :: acc.log, acc.value)
      else aux(m-1, Result(s"${n-m}! = ${acc.value}" :: acc.log, acc.value * m))
    }
    val result =  aux(n, Result(Nil, 1))
    result.log.reverse.foreach(println)
    result.value
  }

  type Logged[A] = Writer[Vector[String], A]

  def factorialMonad(n: Int): Logged[Int] =
      for {
        value <- if (n == 0) 1.pure[Logged] else factorialMonad(n - 1).map(_ * n)
        _ <- Vector(s"${n}! = $value").tell
      } yield value


  //factorial(4)
  //Await.result(Future.sequence(List(Future(factorial(4), factorial(5), factorial(5), factorial(5)))), 5 second)
  //println(factorialTailRec(4))
  //println(factorialMonad(4).run)

  Await.result(Future.sequence(List(Future{runApp()}, Future{runApp()}, Future{runApp()})), 5 second).map(_.written).foreach(println)

  def runApp() = for {
      _ <- service1()
      _ <- service2()
  } yield ()

  def service1() = for {
    _ <- Vector(s"${Thread.currentThread().getName}: executing service1").tell
    _ <- Thread.sleep(Random.nextInt(100)).pure[Logged]
  } yield ()

  def service2() = {
    for {
      _ <- Vector(s"${Thread.currentThread().getName}: executing service2").tell
      _ <- Thread.sleep(Random.nextInt(100)).pure[Logged]
    } yield ()
  }
}
