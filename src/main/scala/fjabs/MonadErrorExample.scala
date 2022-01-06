package fjabs

import cats._
import cats.implicits._

import scala.concurrent.Future
import scala.util.Try
import scala.concurrent.ExecutionContext.Implicits.global

object MonadErrorExample {

  type ErrorOr[A] = Either[Throwable, A]
  type ErrorOr2[A] = Either[String, A]

  def divideThrowable[F[_]](m: Int, n: Int)(implicit monadError: MonadError[F, Throwable]): F[Int] =
    if (n == 0) monadError.raiseError(new Exception("boom"))
    else monadError.pure(m / n)

  def divideString[F[_]](m: Int, n: Int)(implicit monadError: MonadError[F, String]): F[Int] =
    if (n == 0) monadError.raiseError("boom")
    else monadError.pure(m / n)

  def main(args: Array[String]): Unit = {
    val x: Try[Int] = divideThrowable[Try](3,1)
    val y: Future[Int] = divideThrowable[Future](3,1)
    val z: ErrorOr[Int] = divideThrowable[ErrorOr](3,1)
    val z2: ErrorOr2[Int] = divideString[ErrorOr2](3,0)

    println(x)
    println(y)
    println(z)
    println(z2)
  }
}
