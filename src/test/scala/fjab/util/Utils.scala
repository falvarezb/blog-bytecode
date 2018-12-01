package fjab.util

import org.scalatest.FunSuite

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
