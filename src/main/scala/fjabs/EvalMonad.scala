package fjabs

import cats._
import cats.implicits._

object EvalMonad extends App {

  val saying = Eval.
    always {
      println("Step 1"); "The cat"
    }.
    map { str => println("Step 2"); s"$str sat on" }.
    memoize.
    map { str => println("Step 3"); s"$str the mat" }

  println(saying.value)
  println(saying.value)

  //val behaviour: eager, memoized
  val x = Eval.now {
    println("Computing X")
    math.random
  }
  println(x.value)
  println(x.value)

  //def behaviour: lazy, not memoized
  val y = Eval.always {
    println("Computing Y")
    math.random
  }
  println(y.value)
  println(y.value)

  //lazy val behaviour: lazy, memoized
  val z = Eval.later {
    println("Computing Z")
    math.random
  }
  println(z.value)
  println(z.value)

  def factorial(n: BigInt): Eval[BigInt] = {
    if (n == 1) {
      Eval.now(n)
    } else {
      Eval.defer(factorial(n - 1).map(_ * n))
    }
  }

  println(factorial(500).value)

  def foldRight[A, B](as: List[A], acc: B)(fn: (A, B) => B): B = {
    def foldRightEval[A, B](as: List[A], acc: Eval[B])(fn: (A, Eval[B]) => Eval[B]): Eval[B] = as match {
      case head :: tail =>
        Eval.defer(fn(head, foldRightEval(tail, acc)(fn)))
      case Nil => acc
    }
    foldRightEval(as, Eval.now(acc))((a, evalB) => evalB.map(fn(a, _))).value
  }

  println(foldRight((1 to 100000).toList, 0L)(_ + _))
}
