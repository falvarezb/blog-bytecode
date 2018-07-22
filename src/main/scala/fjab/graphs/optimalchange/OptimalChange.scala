package fjab.graphs.optimalchange

import fjab.graphs.{Amount, Coin}
import fjab.graphs.api.GraphTraversal

import scala.collection.mutable.ListBuffer

trait OptimalChange extends GraphTraversal[Coin]{

  self: Coins with Amount =>

  val moves: List[Coin] = coins

  override def neighbours(vertex: Int): List[Int] = moves

  /**
    * The nature of the problem requires a breadth-first search in order to find the shortest path
    */
  override def addNeighbours(verticesToExplore: ListBuffer[Path], neighbours: List[Path]) =
    verticesToExplore ++= neighbours

  override def isSolution(path: Path): Boolean = amount == path.sum

  override def isVertexEligibleForPath(vertex: Int, path: Path): Boolean = (amount - path.sum) >= vertex
}


