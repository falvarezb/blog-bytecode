package fjab.util

import org.scalatest.FunSuite

import scala.annotation.tailrec

class Utils extends FunSuite{

  def xIsPowerOfy(x: BigInt, y: BigInt): Boolean =
    if (x == 1) true
    else if (x % y == 0) xIsPowerOfy(x / y, y)
    else false

  /**
    * Generates list of integers between a and b, both inclusive
    */
  def numberGenerator(a: Int, b: Int): List[Int] =
    if(a == b) List(a)
    else a :: numberGenerator(a + 1, b)


  def highestFrequencyOfOccurrences[T](arr: Array[T]) = arr.groupBy(identity).mapValues(_.length).maxBy(_._2)
  def highestFrequencyOfOccurrences[T,U](arr: Array[T], f: T => U) = arr.groupBy(f).mapValues(_.length).maxBy(_._2)

  def decimalRepresentation(n: BigInt): List[Int] = {
    val q = n/10
    val r = n%10
    if(q == 0) List(r.intValue())
    else r.intValue() :: decimalRepresentation(q)
  }

  def decimalRepresentationTailRecursive(n: BigInt): List[Int] = {

    @tailrec
    def acc(n: BigInt, representation: List[Int]): List[Int] = {
      val q = n/10
      val r = n%10
      if(q == 0) r.intValue() :: representation
      else acc(q, r.intValue() :: representation)
    }

    acc(n, Nil)
  }

  /**
    * Factorial calculation using dynamic programming (with bottom-up memoisation)
    */
  def factorialDP(n: Int): BigInt = {

    val intermediateResults = new Array[BigInt](n+1)
    intermediateResults(0) = 1

    for(i <- Range(1,n+1)){
      intermediateResults(i) = i * intermediateResults(i-1)
    }

    intermediateResults(n)
  }

  test("decimal representation of 9012 is [9,0,1,2]"){
    assert(decimalRepresentationTailRecursive(BigInt(9012)) == List(9,0,1,2))
  }

  test("in array [a,a,a,b,b,c], most frequent element is a with 3 occurrences"){
    assert(highestFrequencyOfOccurrences(Array("a","a","a","b","b","c")) == ("a",3))
  }

  test("in array [1,2,3,4,5], most frequent element is 'odd' with 5 occurrences"){
    assert(highestFrequencyOfOccurrences(Array(1,2,3,4,5), (n:Int) => if(n%2==0) "EVEN" else "ODD") == ("ODD",3))
  }

  test("100 is power of 10"){
    assert(xIsPowerOfy(100,10))
  }

  test("500 is not power of 10"){
    assert(!xIsPowerOfy(500,10))
  }

  test("generate list of numbers between -2 and 3"){
    assert(numberGenerator(-2,3) == List(-2,-1,0,1,2,3))
  }
}
