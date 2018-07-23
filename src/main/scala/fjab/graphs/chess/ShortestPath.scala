package fjab.graphs.chess

import fjab.graphs._
import fjab.graphs.api.GraphTraversal

import scala.collection.mutable.ListBuffer

/**
  * Implementation of GraphTraversal to find the shortest path between 2 vertices of the graph
  *
 */
trait ShortestPath extends GraphTraversal[Coordinate]{

  self: DestinationVertex[Coordinate] =>

  /**
   * The nature of the problem requires a breadth-first search in order to find the shortest path
   */
  override def addNeighbours(verticesToExplore: ListBuffer[Path], neighbours: Seq[Path]): Unit =
    verticesToExplore ++= neighbours

  override def isSolution(path: Path): Boolean = path.head == to
}
