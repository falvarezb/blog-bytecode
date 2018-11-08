package fjab

object Stairs extends App {

  /*
    Number of ways to climb n stairs when it is possible to take 1,2 or 3 steps
    at a time
   */
  def count_combinations(n: Int): Int = n match {
      case 1 => 1 //(1)
      case 2 => 2 //(1,1), (2)
      case 3 => 4 //(1,1,1), (2,1), (1,2), (3)
      case x => List(1,2,3).map(step => count_combinations(x - step)).sum
    }

  def enumerate_combinations(n: Int): List[List[Int]] = n match {
    case 1 => List(List(1)) //(1)
    case 2 => List(List(1,1), List(2)) //(1,1), (2)
    case 3 => List(List(1,1,1), List(2,1), List(1,2), List(3)) //(1,1,1), (2,1), (1,2), (3)
    case x => List(1,2,3).flatMap(step => enumerate_combinations(x - step).map(comb => step :: comb))
  }

  println(count_combinations(4))
  println(enumerate_combinations(4))

}
