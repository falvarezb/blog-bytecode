package fjab.graphs.chess

case class KnightTourInFiniteBoard(x: Int, y: Int) extends KnightTour with FiniteChessBoard with BoardDim

object KnightTourInFiniteBoard{
  def apply(x: Int, y: Int): KnightTourInFiniteBoard = new KnightTourInFiniteBoard(x, y)
}
