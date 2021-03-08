package fjabs.functor



trait Functor[F[_]] {
  def map[A, B](fa: F[A])(f: A => B): F[B]
}

object Functor {

  //def apply[F, A]: Functor[F] = implicitly[Functor[F]]

  implicit val optionFunctor = new Functor[Option] {
    override def map[A, B](fa: Option[A])(f: A => B): Option[B] = fa match {
      case None => None
      case Some(x) => Some(f(x))
    }
  }
}

trait Applicative[F[_]] extends Functor[F] {
  def ap[A, B](ff: F[A => B])(fa: F[A]): F[B]

  def pure[A](a: A): F[A]

  def map[A, B](fa: F[A])(f: A => B): F[B] = ap(pure(f))(fa)

  def product[A, B](fa: F[A], fb: F[B]): F[(A, B)] = {
    val x: A => B => (A, B) = (a: A) => (b: B) => (a, b)
    val y: F[B => (A, B)] = map(fa)(x)
    ap(y)(fb)
  }


}

object Applicative {

  //def apply[F: Applicative]: Applicative[F] = implicitly[Applicative[F]]

  implicit val optionApplicative = new Applicative[Option] {
    override def ap[A, B](ff: Option[A => B])(fa: Option[A]): Option[B] = ff match {
      case None => None
      case Some(f) => fa match {
        case Some(a) => Some(f(a))
        case None => None
      }
    }

    override def pure[A](a: A): Option[A] = Some(a)
  }
}

object Main extends App {
  def combine[F[_], A, B](fa: F[A], fb: F[B])(implicit  ap: Applicative[F]): F[(A, B)] = ap.product(fa, fb)

  println(combine(Option("dd"), Option(1)))
}
