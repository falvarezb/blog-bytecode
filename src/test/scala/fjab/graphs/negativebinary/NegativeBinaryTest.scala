package fjab.graphs.negativebinary

import org.scalatest.FunSuite


class NegativeBinaryTest extends FunSuite {


  test("0 == {0}"){
    val number = 0
    assert(NegativeBinaryApp(number).findShortestBinaryRepresentation() == List(0))
  }

  test("1 == {1}"){
    val number = 1
    assert(NegativeBinaryApp(number).findShortestBinaryRepresentation() == List(1))
  }

  test("2 == {0,1,1}"){
    assert(NegativeBinaryApp(2).findShortestBinaryRepresentation() == List(0,1,1))
  }

  test("-2 == {0,1}"){
    val number = -2
    assert(NegativeBinaryApp(number).findShortestBinaryRepresentation() == List(0,1))
  }


  test("-9 == {1,1,0,1}"){
    val number = -9
    assert(NegativeBinaryApp(number).findShortestBinaryRepresentation() == List(1,1,0,1))
  }

  test("9 == {1,0,0,1,1}"){
    val number = 9
    assert(NegativeBinaryApp(number).findShortestBinaryRepresentation() == List(1,0,0,1,1))
  }

  test("-23 == {1,0,0,1,1,1}"){
    val number = -23
    assert(NegativeBinaryApp(number).findShortestBinaryRepresentation() == List(1,0,0,1,1,1))
  }

  test("23 == {1,1,0,1,0,1,1}"){
    val number = 23
    assert(NegativeBinaryApp(number).findShortestBinaryRepresentation() == List(1,1,0,1,0,1,1))
  }


  test("NegativeBinary performance"){
    val number = 50000
    assert(NegativeBinaryApp(number).findShortestBinaryRepresentation() == List(0, 0, 0, 0, 1, 0, 1, 0, 1, 1, 1, 0, 0, 0, 1, 1, 1))
  }
}
