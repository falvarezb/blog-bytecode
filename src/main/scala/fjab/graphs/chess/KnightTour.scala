package fjab.graphs.chess

import fjab.graphs._
import fjab.graphs.api.GraphTraversal

import scala.collection.mutable.ListBuffer

/**
  * Implementation of GraphTraversal to find the path that visits all and every square just once
  *
  */
trait KnightTour extends GraphTraversal[Coordinate]{

  self: FiniteChessBoard with BoardDim =>

  override def addNeighbours(verticesToExplore: ListBuffer[Path], neighbours: Seq[Path]): Unit =
    verticesToExplore.prependAll(neighbours) //depth-first search

  override def isSolution(path: Path): Boolean = path.length == x * y
}
