package fjab.graphs.chess

case class KnightTourInFiniteBoardApp(x: Int, y: Int) extends KnightTour with FiniteChessBoard with BoardDim

object KnightTourInFiniteBoardApp{
  def apply(x: Int, y: Int): KnightTourInFiniteBoardApp = new KnightTourInFiniteBoardApp(x, y)
}
