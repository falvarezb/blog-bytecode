package fjabs.functor

import fjabs.functor.Functor.FunctorOps

import scala.annotation.tailrec

sealed abstract class Tree[+A]
final case class Leaf[A](value: A) extends Tree[A]
final case class Branch[A](eft: Tree[A], right: Tree[A]) extends Tree[A]

trait Functor[F[_]] {
  def map[A, B](fa: F[A])(f: A => B): F[B]
  def lift[A, B](f: A => B): F[A] => F[B] = fa => map(fa)(f)
}

object Functor {

  def apply[F[_]](implicit ev: Functor[F]): Functor[F] = ev
  //def apply[F[_]: Functor]: Functor[F] = implicitly[Functor[F]]

  implicit val optionFunctor = new Functor[Option] {
    override def map[A, B](fa: Option[A])(f: A => B): Option[B] = fa match {
      case None => None
      case Some(x) => Some(f(x))
    }
  }

  type F1[A] = Function1[String, A]
  implicit val function1Functor = new Functor[F1] {
    override def map[A, B](fa: F1[A])(f: A => B): F1[B] = str => f(fa(str))
  }

  implicit val treeFunctor = new Functor[Tree] {
    override def map[A, B](fa: Tree[A])(f: A => B): Tree[B] = fa match {
      case Leaf(value) => Leaf(f(value))
      case Branch(left, right) => Branch(map(left)(f), map(right)(f))
    }
  }

  implicit class FunctorOps[F[_], A](src: F[A]) {
    def map[B](func: A => B)(implicit functor: Functor[F]): F[B] =
      functor.map(src)(func)
  }
}

// ===================================
// ===== Contravariant functors ======
// ===================================

trait Printable[A] {
  self =>
  def format(a: A): String

  def contramap[B](f: B => A): Printable[B] = new Printable[B] {
    override def format(b: B): String = self.format(f(b))
  }
}

object Printable {
  def apply[A](implicit ev: Printable[A]): Printable[A] = ev

  implicit val intInstance = new Printable[Int] {
    override def format(a: Int): String = a.toString
  }

  implicit val doubleInstance = intInstance.contramap[Double](Math.round(_).toInt)
}

// ===================================
// ===== Invariant functors ==========
// ===================================

trait Codec[A] {
  self =>
  def encode(value: A): String
  def decode(value: String): A
  def imap[B](dec: A => B, enc: B => A): Codec[B] = new Codec[B] {
    override def encode(value: B): String = self.encode(enc(value))
    override def decode(value: String): B = dec(self.decode(value))
  }
}

object Codec {
  def apply[A: Codec]: Codec[A] = implicitly[Codec[A]]

  implicit val strincCodec: Codec[String] = new Codec[String] {
    override def encode(value: String): String = value
    override def decode(value: String): String = value
  }

  implicit val intCodec: Codec[Int] = strincCodec.imap[Int](_.toInt, _.toString)
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
  def print[A](a: A)(implicit p: Printable[A]): String = p.format(a)
  def encode[A](a: A)(implicit c: Codec[A]): String = c.encode(a)
  def decode[A](s: String)(implicit c: Codec[A]): String = c.decode(s)

  println(combine(Option("dd"), Option(1)))

  val tree: Tree[Int] = Branch(Branch(Leaf(1), Leaf(2)), Leaf(3))
  println(Functor[Tree].map(tree)(_*2))
  println(tree.map(_*2))

  println(print(2.8))


}
