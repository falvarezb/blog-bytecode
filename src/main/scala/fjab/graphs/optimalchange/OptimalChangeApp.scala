package fjab.graphs.optimalchange

import fjab.graphs.{Amount, Coin}

class OptimalChangeApp(val coins: List[Coin], val amount: Int) extends OptimalChange with Coins with Amount{
  def optimalChange() = findPath(List(List(0))) match{
    case Nil => Nil
    case xs => xs.tail
  }
}

object OptimalChangeApp {
  def apply(coins: List[Coin], amount: Int): OptimalChangeApp = new OptimalChangeApp(coins,amount)
}

