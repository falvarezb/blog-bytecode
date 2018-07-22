package fjab.graphs.chess

import fjab.graphs._

/**
  * Implementation of GraphTraversal corresponding to a finite chess board
 */
trait FiniteChessBoard extends InfiniteChessBoard{

  self: BoardDim =>

  override def neighbours(coordinate: Coordinate): List[Coordinate] =
    super.neighbours(coordinate).filter{ case (v, w) => v >= 0 && v<x && w >=0 && w<y }
}
