package fjabs

import fjabs.Branch.branch
import fjabs.FoldableAPI._

sealed abstract class Tree[A]
final case class Leaf[A](value: A) extends Tree[A]
final case class Branch[A](value: A, left: Tree[A], right: Tree[A]) extends Tree[A]
object Branch {
  //smart constructor: upcast Branch to Tree
  def branch[A](value: A, left: Tree[A], right: Tree[A]): Tree[A] = Branch(value, left, right)
}

//FoldLeft represents a type that can be folded from left to right
trait FoldLeft[F[_]] {
  def foldLeft[A, B](xs: F[A], b: B, f: (B, A) => B): B
}

//FoldLeft represents a type that can be folded without any specific order
trait Fold[F[_]] {
  def fold[A](xs: F[A], b: A, f: (A, A) => A): A
}

object Fold {

  def apply[F[_]: Fold]: Fold[F] = implicitly[Fold[F]]

  implicit val foldTree: Fold[Tree] = new Fold[Tree] {
    override def fold[A](xs: Tree[A], b: A, f: (A, A) => A): A = xs match {
      case Leaf(v) => f(b,v)
      case Branch(v,l,r) => f(f(fold(l,b,f), v),fold(r,b,f))
    }
  }
}

object FoldLeft {

  def apply[F[_]: FoldLeft]: FoldLeft[F] = implicitly[FoldLeft[F]]

  implicit val foldLeftList: FoldLeft[List] = new FoldLeft[List] {
    override def foldLeft[A, B](xs: List[A], b: B, f: (B, A) => B): B = xs.foldLeft(b)(f)
  }

  implicit val foldLefTree: FoldLeft[Tree] = new FoldLeft[Tree] {
    override def foldLeft[A, B](xs: Tree[A], b: B, f: (B, A) => B): B = xs match {
      case Leaf(v) => f(b,v)
      case Branch(v,l,r) => foldLeft(r, foldLeft(l, f(b,v), f), f)
    }
  }
}

object FoldableAPI {

  def sum[F[_]: FoldLeft](xs: F[Int]): Int = {
    FoldLeft[F].foldLeft[Int, Int](xs, 0, (acc, x) => acc + x)
  }

  def concatenate[F[_]: FoldLeft](xs: F[String]): String = {
    FoldLeft[F].foldLeft[String, String](xs, "", (acc, x) => acc + x)
  }

  def concatenate2[F[_]: Fold](xs: F[String]): String = {
    Fold[F].fold[String](xs, "", (acc, x) => acc + x)
  }
}

object Main extends App {

  println(sum(List(1,2,3,4)))
  println(sum(branch(1, Branch(1, Leaf(2), Leaf(3)), Leaf(8))))
  println(concatenate(branch("1", Branch("1", Leaf("2"), Leaf("3")), Leaf("8"))))
  println(concatenate2(branch("1", Branch("1", Leaf("2"), Leaf("3")), Leaf("8"))))
}

object CatsFoldable extends App {

  import cats._
  import cats.implicits._

  implicit val foldableTree = new Foldable[Tree] {
    override def foldLeft[A, B](fa: Tree[A], b: B)(f: (B, A) => B): B = fa match {
      case Leaf(v) => f(b,v)
      case Branch(v,l,r) => foldLeft(r, foldLeft(l, f(b,v))(f))(f)
    }

    override def foldRight[A, B](fa: Tree[A], lb: Eval[B])(f: (A, Eval[B]) => Eval[B]): Eval[B] = ???
  }

  println(Foldable[Tree].fold(branch("1", Branch("1", Leaf("2"), Leaf("3")), Leaf("8"))))
}
