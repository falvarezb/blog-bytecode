package fjab.graphs.chess

import fjab.graphs.Coordinate

case class ShortestPathInFiniteBoardApp(x: Int, y: Int, to: Coordinate) extends FiniteChessBoard with ShortestPath with BoardDim with Destination[Coordinate]

object ShortestPathInFiniteBoardApp{
  def apply(x: Int, y: Int, to: Coordinate): ShortestPathInFiniteBoardApp = new ShortestPathInFiniteBoardApp(x, y, to)
}
