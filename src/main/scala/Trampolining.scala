import scala.util.Try
import scala.util.control.TailCalls.TailRec
import util.control.TailCalls._

/*
  Learn scala programming by Slava Schmidt

  Trampolining is replacing recursive function calls with objects representing these calls.
  This way the recursive computation is built up in the heap instead of the stack, and it is possible
  to represent much deeper recursive calls just because of the bigger size of the heap

  There is also an approach to model recursive calls in an object-oriented way called trampolining
 */

object Trampolining extends App {

  var stackCounter = 0
  //Ackerman function
  val A: (Long, Long) => Long = (m, n) => {
    stackCounter += 1
    if (m == 0) n + 1
    else if (n == 0) A(m - 1, 1)
    else A(m - 1, A(m, n - 1))
  }

  try{println(A(4,2)) }catch{case t:Throwable => }//StackOverFlow
  println(stackCounter)

  def tailA(m: BigInt, n: BigInt): TailRec[BigInt] = {
    if (m == 0) done(n + 1)
    else if (n == 0) tailcall(tailA(m - 1, 1))
    else tailcall(tailA(m, n - 1)).flatMap(tailA(m - 1, _))
  }
  def tailRecA(m: Int, n: Int): BigInt = tailA(m, n).result

  //println(tailRecA(4,2))


  def isEvenRec(xs: List[Int]): Boolean = {stackCounter += 1;if(xs.isEmpty) true else isOddRec(xs.tail)}
  def isOddRec(xs: List[Int]): Boolean = {stackCounter += 1;if(xs.isEmpty) false else isEvenRec(xs.tail)}

  def isEven(xs: List[Int]): TailRec[Boolean] =
    if (xs.isEmpty) done(true) else tailcall(isOdd(xs.tail))

  def isOdd(xs: List[Int]): TailRec[Boolean] =
    if (xs.isEmpty) done(false) else tailcall(isEven(xs.tail))

//  println(isEven((1 to 100001).toList).result)
//  try{println(isEvenRec((1 to 100001).toList))}catch{case t:Throwable => }
//  println(stackCounter) //12131
}
