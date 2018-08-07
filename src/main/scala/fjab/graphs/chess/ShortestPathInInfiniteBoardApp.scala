package fjab.graphs.chess

import fjab.graphs._

case class ShortestPathInInfiniteBoardApp(to: Coordinate) extends InfiniteChessBoard with ShortestPath with Destination[Coordinate]

object ShortestPathInInfiniteBoardApp{
  def apply(to: Coordinate): ShortestPathInInfiniteBoardApp = new ShortestPathInInfiniteBoardApp(to)
}
