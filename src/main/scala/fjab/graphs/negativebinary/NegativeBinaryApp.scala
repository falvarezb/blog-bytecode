package fjab.graphs.negativebinary

import fjab.graphs.Amount

class NegativeBinaryApp(val amount: Int) extends NegativeBinary with Amount{

  def findShortestBinaryRepresentation() = findPath(List(List(0),List(1)))
}

object NegativeBinaryApp {
  def apply(amount: Int): NegativeBinaryApp = new NegativeBinaryApp(amount)
}
