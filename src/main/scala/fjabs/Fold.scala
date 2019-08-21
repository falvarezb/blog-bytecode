package fjabs

sealed abstract class Tree[A]
final case class Leaf[A](value: A) extends Tree[A]
final case class Branch[A](value: A, left: Tree[A], right: Tree[A]) extends Tree[A]
object Branch {
  def branch[A](value: A, left: Tree[A], right: Tree[A]): Tree[A] = Branch(value, left, right)
}

//FoldLeft represents a type that can be folded
trait FoldLeft[F[_]] {
  def foldLeft[A, B](xs: F[A], b: B, f: (B, A) => B): B
}

trait Fold[F[_]] {
//  def foldLeft[A](xs: F[A], b: A, f: (A, A) => A): A
//  def foldRight[A](xs: F[A], b: A, f: (A, A) => A): A
  def fold[A](xs: F[A], b: A, f: (A, A) => A): A
}

object Fold{

  def apply[F[_]: Fold]: Fold[F] = implicitly[Fold[F]]

  implicit val foldTree: Fold[Tree] = new Fold[Tree] {
//    override def foldLeft[A](xs: Tree[A], b: A, f: (A, A) => A): A = xs match {
//      case Leaf(v) => f(b,v)
//      case Branch(v,l,r) => foldLeft(r, f(b,v), f)
//    }
//
//    override def foldRight[A](xs: Tree[A], b: A, f: (A, A) => A): A = xs match {
//      case Leaf(v) => f(b,v)
//      case Branch(v,l,r) => foldRight(l, f(v,b), f)
//    }

    override def fold[A](xs: Tree[A], b: A, f: (A, A) => A): A = xs match {
      case Leaf(v) => f(b,v)
      case Branch(v,l,r) => f(f(fold(l,b,f), v),fold(r,b,f))
    }
  }
}

object FoldLeft{

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

object Foldable extends App {

  import Branch._

  //Sum over the elements of a type as long as it is foldable and there is a Monoid for its elements
  def sum[F[_]: FoldLeft, A: Monoid](xs: F[A]): A = {
    FoldLeft[F].foldLeft[A,A](xs, Monoid[A].empty, Monoid[A].combine)
  }

  def sum2[F[_]: Fold, A: Monoid](xs: F[A]): A = {
    Fold[F].fold[A](xs, Monoid[A].empty, Monoid[A].combine)
  }

  println(sum(List(1,2,3,4)))
  println(sum(List("1","2","3")))
  println(sum(List(1,2,3,4))(FoldLeft.foldLeftList, Monoid.multiplicativeMonoid))

  //val tree: Tree[Int] = Branch(1, Branch(1, Leaf(2), Leaf(3)), Leaf(8))
  //println(FoldLeft.foldLefTree.foldLeft(tree, 0, (x: Int, y: Int) => x+y))

  println(sum(branch(1, Branch(1, Leaf(2), Leaf(3)), Leaf(8))))
  println(sum(branch("1", Branch("2", Leaf("3"), Leaf("4")), Branch("5", Leaf("6"), Leaf("7")))))
  //println(sum(empty[Int]))

  println(sum2(branch(1, Branch(1, Leaf(2), Leaf(3)), Leaf(8))))
  println(sum2(branch("1", Branch("2", Leaf("3"), Leaf("4")), Branch("5", Leaf("6"), Leaf("7")))))
  //println(sum2(empty[Int]))

  val xs = List(1,2,3)
  println(xs.reduceLeft((acc,x) => acc + x))
  println(xs.reduceLeft((acc,x) => acc - x))
  println(xs.reduceRight((x, acc) => x - acc))
  println(xs.reduce((x, y) => x - y))

  println("")
  println(xs.foldLeft(0)((acc,x) => acc - x))
  println(xs.foldRight(0)((x, acc) => x - acc))
  println(xs.fold(0)((x, y) => x - y))

  println("")

  def sumLoop(xs: List[Int]): Int = {
    var acc = 0
    for(x <- xs){
      acc += x
    }
    acc
  }


  def sumRecursive(xs: List[Int]): Int = xs match {
    case Nil => 0
    case x :: tail => x + sumRecursive(tail)
  }


  def sumFold(xs: List[Int]): Int = xs.reduce((acc, x) => acc + x)

  println(sumLoop(xs))
  println(sumRecursive(xs))
  println(sumFold(xs))
}
