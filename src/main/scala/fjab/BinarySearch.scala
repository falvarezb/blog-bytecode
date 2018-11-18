package fjab

object BinarySearch extends App {

  def solution(arr: List[Int], valueSearched: Int, lowerBound: Int, upperBound: Int): Option[Int] = {

    if(lowerBound > upperBound) None
    else {
      val mid = (lowerBound + upperBound) / 2
      if (arr(mid) == valueSearched) Some(mid)
      else if (valueSearched < arr(mid)) solution(arr, valueSearched, lowerBound, mid - 1)
      else solution(arr, valueSearched, mid + 1, upperBound)
    }
  }

  def solution2(arr: List[Int], valueSearched: Int, lowerBound: Int, upperBound: Int): Int = {

    if(lowerBound > upperBound) lowerBound - 1
    else {
      val mid = (lowerBound + upperBound) / 2
      if (valueSearched <= arr(mid)) solution2(arr, valueSearched, lowerBound, mid - 1)
      else solution2(arr, valueSearched, mid + 1, upperBound)
    }
  }

  val l = List(Integer.MIN_VALUE,1,3,4,7,10,21,26)
  println(solution2(l, 3, 0, l.length-1))

}
