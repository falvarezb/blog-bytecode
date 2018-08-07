package fjab.graphs.chess

import org.scalatest.FunSpec

class ShortestPathTest extends FunSpec {

  describe("shortest path between (0,0) -> (3,3)"){
    val from = (0,0)
    val to = (3,3)
    val solution = List((0,0), (2,1), (3,3))
    it("in infinite board"){
      assert(ShortestPathInInfiniteBoardApp(to).findPath(List(List(from))) == solution)
    }
    it("in finite board of dimensions (4,4)"){
      val dim = (4,4)
      assert(ShortestPathInFiniteBoardApp(dim._1,dim._2, to).findPath(List(List(from))) == solution)
    }
    it("shared knowledge in infinite board"){
      assert(SKShortestPathInInfiniteBoardApp(to).findPath(List(List(from))) == solution)
    }
  }

  describe("shortest path between (0,0) -> (8,1)"){
    val from = (0,0)
    val to = (8,1)
    val solution = List((0,0), (2,1), (4,2), (5,4), (6,2), (8,1))
    it("in infinite board"){
      assert(ShortestPathInInfiniteBoardApp(to).findPath(List(List(from))) == solution)
    }
    it("in finite board of dimensions (4,4)"){
      val dim = (4,4)
      assert(ShortestPathInFiniteBoardApp(dim._1, dim._2, to).findPath(List(List(from))) == List())
    }
    it("shared knowledge in infinite board"){
      assert(SKShortestPathInInfiniteBoardApp(to).findPath(List(List(from))) == solution)
    }
  }


  describe("performance comparison of basic and shared knowledge algorithms"){
    val from = (0,0)
    describe("the shortest path to (10,10)"){
      val to = (10,10)
      val solution = List((0, 0), (2, 1), (4, 2), (6, 3), (8, 4), (10, 5), (12, 6), (11, 8), (10, 10))
      it("with Shared Knowledge Algorithm") {
        assert(SKShortestPathInInfiniteBoardApp(to).findPath(List(List(from))) == solution)
      }
      it("with Basic Algorithm") {
        assert(ShortestPathInInfiniteBoardApp(to).findPath(List(List(from))) == solution)
      }
    }

    describe("the shortest path to (15,15)"){
      val to = (15,15)
      val solution = List((0,0), (2,1), (4,2), (6,3), (8,4), (10,5), (11,7), (12,9), (13,11), (14,13), (15,15))
      it("with Shared Knowledge Algorithm") {
        assert(SKShortestPathInInfiniteBoardApp(to).findPath(List(List(from))) == solution)
      }
      ignore("with Basic Algorithm") {
        assert(ShortestPathInInfiniteBoardApp(to).findPath(List(List(from))) == solution)
      }
    }
  }


}
