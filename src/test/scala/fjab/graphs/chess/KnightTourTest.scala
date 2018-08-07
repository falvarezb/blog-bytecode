package fjab.graphs.chess

import org.scalatest.FunSpec

class KnightTourTest extends FunSpec{

  describe("in a board of dimensions (6,6)"){
    val dim = (6,6)
    describe("starting at (0,0)"){
      val from = (0,0)
      val solution = List((0,0), (2,1), (4,2), (5,4), (3,5), (1,4), (0,2), (2,3), (4,4), (2,5), (0,4), (1,2), (2,4), (0,5), (1,3), (0,1), (2,0), (4,1), (5,3), (4,5), (3,3), (5,2), (4,0), (3,2), (1,1), (0,3), (1,5), (3,4), (5,5), (4,3), (5,1), (3,0), (2,2), (1,0), (3,1), (5,0))
      it("the path should be "){
        assert(KnightTourInFiniteBoardApp(dim._1, dim._2).findPath(List(List(from))) == solution)
      }
    }

    describe("starting at (1,1)"){
      val from = (1,1)
      val solution = List((1,1), (3,2), (5,3), (4,5), (2,4), (0,5), (1,3), (3,4), (5,5), (4,3), (5,1), (3,0), (4,2), (5,0), (3,1), (5,2), (4,0), (2,1), (0,0), (1,2), (0,4), (2,5), (4,4), (2,3), (1,5), (0,3), (2,2), (0,1), (2,0), (4,1), (3,3), (5,4), (3,5), (1,4), (0,2), (1,0))
      it("the path should be "){
        assert(KnightTourInFiniteBoardApp(dim._1, dim._2).findPath(List(List(from))) == solution)
      }
    }
  }

  describe("in a board of dimensions (8,8)"){
    val dim = (8,8)
    describe("starting at (0,0)"){
      val from = (0,0)
      val solution = List((0,0), (2,1), (4,2), (6,3), (7,5), (6,7), (4,6), (2,7), (0,6), (1,4), (3,5), (5,6), (7,7), (6,5), (5,7), (3,6), (1,7), (0,5), (2,6), (4,7), (5,5), (7,6), (6,4), (4,5), (6,6), (5,4), (3,3), (2,5), (3,7), (1,6), (0,4), (1,2), (2,4), (0,3), (1,1), (3,0), (2,2), (1,0), (0,2), (2,3), (4,4), (3,2), (4,0), (6,1), (7,3), (5,2), (7,1), (5,0), (3,1), (4,3), (5,1), (7,0), (6,2), (7,4), (5,3), (7,2), (6,0), (4,1), (2,0), (0,1), (1,3), (3,4), (1,5), (0,7))
      it("the path should be "){
        assert(KnightTourInFiniteBoardApp(dim._1, dim._2).findPath(List(List(from))) == solution)
      }
    }

    ignore("starting at (1,1)"){
      val from = (1,1)
      val solution = List((1,1), (3,2), (5,3), (4,5), (2,4), (0,5), (1,3), (3,4), (5,5), (4,3), (5,1), (3,0), (4,2), (5,0), (3,1), (5,2), (4,0), (2,1), (0,0), (1,2), (0,4), (2,5), (4,4), (2,3), (1,5), (0,3), (2,2), (0,1), (2,0), (4,1), (3,3), (5,4), (3,5), (1,4), (0,2), (1,0))
      ignore("the path should be "){
        assert(KnightTourInFiniteBoardApp(dim._1, dim._2).findPath(List(List(from))) == solution)
      }
    }
  }

}
