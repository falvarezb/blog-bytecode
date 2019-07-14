package fjabs

import org.scalatest.FunSuite

import scala.util.Random

/*
  Functional Programming in Scala, Chapter 6
 */

trait RNG {
  def nextInt: (Int, RNG)
}

object RNG {

  def simple(seed: Long): RNG = new RNG {

    def nextInt = {
      val seed2 = (seed*0x5DEECE66DL + 0xBL) &
        ((1L << 48) - 1)
      ((seed2 >>> 16).asInstanceOf[Int],
        simple(seed2))
    }

  }
}

object Functions {

  /**
    * function to generate a random positive integer
    */
  def positiveInt(rng: RNG): (Int, RNG) = {
    val (i, nextRng) = rng.nextInt
    if(i == Int.MinValue) positiveInt(nextRng) else (i, nextRng)
  }

  /**
    * function to generate a Double between 0 and 1, not including 1
    */
  def double(rng: RNG) = {
    val (i, s) = positiveInt(rng)
    (i.toDouble/(Int.MaxValue+1), s)
  }

  def intDouble(rng: RNG) = {
    val (i, s1) = positiveInt(rng)
    val (d, s2) = double(s1)

    ((i, d), s2)
  }

  def double3(rng: RNG) = {
    val (d1, s1) = double(rng)
    val (d2, s2) = double(s1)
    val (d3, s3) = double(s2)

    ((d1, d2, d3), s3)
  }

  def ints(count: Int)(rng: RNG): (List[Int], RNG) = {
    (1 to count).foldLeft((List.empty[Int], rng))((acc, _) => {
      val (l, currentRng) = acc
      val (a, nextRng) = currentRng.nextInt
      (a :: l, nextRng)
    })
  }


  type Rand[+A] = RNG => (A, RNG)

  val int: Rand[Int] = _.nextInt

  def unit[A](a: A): Rand[A] = rng => (a, rng)

  def map[A,B](s: Rand[A])(f: A => B): Rand[B] = rng => {
      val (a, nextRng) = s(rng)
      (f(a), nextRng)
    }


  def positiveMax(n: Int): Rand[Int] = map(positiveInt)(_ % n)

  def _double: Rand[Double] = map(positiveInt)(_.toDouble/(Int.MaxValue+1))

  def map2[A,B,C](ra: Rand[A], rb: Rand[B])(f: (A, B) => C): Rand[C] = rng => {
      val (a, rng2) = ra(rng)
      val (b, rng3) = rb(rng2)

      (f(a,b), rng3)
    }


  def _intDouble: Rand[(Int, Double)] = map2(positiveInt, double)((_,_))

  def sequence[A](fs: List[Rand[A]]): Rand[List[A]] = fs.foldLeft(unit(List.empty[A]))((acc, f) => map2(f,acc)(_ :: _))

  def traverse[A,B](fs: List[Rand[A]])(g: A => B): Rand[List[B]] = fs.foldLeft(unit(List.empty[B]))((acc, f) => map2(f,acc)(g(_) :: _))

  def _ints(count: Int): Rand[List[Int]] = sequence(List.fill(count)(int))

  def flatMap[A,B](f: Rand[A])(g: A => Rand[B]): Rand[B] = rng => {
    val (a, nextRng) = f(rng)
    g(a)(nextRng)
  }

  def _positiveInt: Rand[Int] = flatMap(int)(i => rng => if(i == Int.MinValue) int(rng) else (i, rng))

  def _map[A,B](s: Rand[A])(f: A => B): Rand[B] = flatMap(s)(a => unit(f(a)))

  def _map2[A,B,C](ra: Rand[A], rb: Rand[B])(f: (A, B) => C): Rand[C] = flatMap(ra)(a => map(rb)(b => f(a,b)))

  type State[S,+A] = S => (A,S)

  def sunit[S,A](a: A): State[S,A] = s => (a, s)

  def sflatMap[S,A,B](f: State[S,A])(g: A => State[S,B]): State[S,B] = r => {
    val (a, nextR) = f(r)
    g(a)(nextR)
  }

  def smap[S,A,B](s: State[S,A])(f: A => B): State[S,B] = sflatMap(s)(a => sunit(f(a)))

  def smap2[S,A,B,C](ra: State[S,A], rb: State[S,B])(f: (A, B) => C): State[S,C] = sflatMap(ra)(a => smap(rb)(b => f(a,b)))

}

case class State[S,+A](run: S => (A,S)) {

  def unit[A](a: A): State[S,A] = State(s => (a,s))

  def map[A,B](s: State[S,A])(f: A => B): State[S, B] = State(rng => {
    val (a, nextRng) = s.run(rng)
    (f(a), nextRng)
  })

  def map2[A,B,C](ra: State[S,A], rb: State[S,B])(f: (A, B) => C): State[S,C] = State(rng => {
    val (a, rng2) = ra.run(rng)
    val (b, rng3) = rb.run(rng2)

    (f(a,b), rng3)
  })

  def flatMap[A,B](f: State[S,A])(g: A => State[S,B]): State[S,B] = State(rng => {
    val (a, nextRng) = f.run(rng)
    g(a).run(nextRng)
  })

  def sequence[A](fs: List[State[S,A]]): State[S,List[A]] = State(rng => {

    fs.foldLeft((List.empty[A], rng))((acc, rand) => {
      val (l, currentRng) = acc
      val (a, nextRng) = rand.run(currentRng)
      (a :: l, nextRng)
    })
  })
}

class FunctionalState extends FunSuite{

  import Functions._

  test("RNG.nextInt is referentially transparent") {
    val seed = 123
    val (int, _) = RNG.simple(seed).nextInt
    assert(int == RNG.simple(seed).nextInt._1)
  }

  test("Random.nextInt is not referentially transparent") {
    val seed = 123
    val int = Random.nextInt()
    assert(int != Random.nextInt())
  }

  test("Int.minValue") {
    assert(Int.MinValue == -Int.MinValue)
  }

  test("ints") {
    println(positiveInt(RNG.simple(0))._1)
    println(double(RNG.simple(1))._1)
    println(ints(5)( RNG.simple(2))._1)
    println(_ints(5)( RNG.simple(2))._1)
  }

}
