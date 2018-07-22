package fjab.graphs.chess

import fjab.graphs._
import fjab.graphs.api.GraphTraversal

import scala.collection.mutable.ListBuffer

/**
  * Implementation of GraphTraversal to find the shortest path between 2 vertices of the graph
  *
 */
trait KnightTour extends GraphTraversal[Coordinate]{

  self: FiniteChessBoard with BoardDim =>

  override def addNeighbours(verticesToExplore: ListBuffer[Path], neighbours: List[Path]) =
    verticesToExplore.prependAll(neighbours) //depth-first search

  override def isSolution(path: Path): Boolean = path.length == x * y
}
