package fjab.graphs.chess

import fjab.graphs._
import fjab.graphs.api.GraphTraversal


/**
  * Implementation of GraphTraversal representing an infinite chess board
  *
 */
trait InfiniteChessBoard extends GraphTraversal[Coordinate]{

  //knight moves
  val moves: List[Coordinate] = List((2,1), (1,2), (-1,2), (-2,1), (-2,-1), (-1,-2), (1,-2), (2,-1))

  override def neighbours(coordinate: Coordinate): Seq[Coordinate] = moves.map( coordinate + _)

  /**
    * Avoid an infinite loop by not visiting previously visited vertices in the present path
    */
  override def isVertexEligibleForPath(vertex: Coordinate, path: Path): Boolean = !path.contains(vertex)
}
