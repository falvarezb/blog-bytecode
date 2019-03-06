package fjabs

//FoldLeft represents a structure (a type constructor) that can be folded
trait FoldLeft[F[_]] {
  def foldLeft[A, B](xs: F[A], b: B, f: (B, A) => B): B
}

object FoldLeft{

  implicit val foldLeftList: FoldLeft[List] = new FoldLeft[List] {
    def foldLeft[A, B](xs: List[A], b: B, f: (B, A) => B) = xs.foldLeft(b)(f)
  }
}

object Foldable extends App {

  //Sum over the elements of a data structure as long as that data structure is foldable and there is a Monoid for its elements
  def sum[M[_], A](xs: M[A])(implicit fl: FoldLeft[M], m: Monoid[A]): A = {
    fl.foldLeft(xs, m.mzero, m.mappend)
  }

  println(sum(List(1,2,3,4)))
  println(sum(List("1","2","3")))
  println(sum(List(1,2,3,4))(FoldLeft.foldLeftList, Monoid.multiplicativeMonoid))
}
