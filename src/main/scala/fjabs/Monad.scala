package fjabs.monad

import fjabs.monad.Monad.idMonad
import fjabs.monad.Types.Id

object Types {
  type Id[A] = A
}

trait Monad[F[_]] {
  def pure[A](a: A): F[A]
  def flatMap[A, B](value: F[A])(f: A => F[B]): F[B]
  def map[A, B](value: F[A])(f: A => B): F[B] = flatMap(value)(a => pure(f(a)))
}

object Monad {

  /**
   * flatMap and map are identical
   * This chimes in with our understanding of functors and monads as sequencing type classes.
   * Each type class allows us to sequence operations ignoring some kind of complication.
   * In the case of Id there is no complication, making map and flatMap the same thing.
   */
  val idMonad: Monad[Id] = new Monad[Id] {
    override def pure[A](a: A): Id[A] = a
    override def flatMap[A, B](value: Id[A])(f: A => Id[B]): Id[B] = f(value)
    override def map[A, B](value: Id[A])(f: A => B): Id[B] = f(value)
  }
}

object Main extends App {

  println(idMonad.pure(5))
  println(idMonad.flatMap(5)(_ * 2))
}
