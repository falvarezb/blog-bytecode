package fjab.graphs.chess

import fjab.graphs._


/**
  * Given an infinite chessboard and a knight, calculate the shortest path to a target square
  *
  * The implementation of the method neighbours represent the possible moves of a knight, making this an example
  * of undirected cyclic graph. Therefore it is crucial that the implementation of the method isVertexEligibleForPath
  * does not let a path visit the same vertex twice
 */
case class ShortestPathInInfiniteBoard(to: Coordinate) extends InfiniteChessBoard with ShortestPath with DestinationVertex[Coordinate]

object ShortestPathInInfiniteBoard{
  def apply(to: Coordinate): ShortestPathInInfiniteBoard = new ShortestPathInInfiniteBoard(to)
}
