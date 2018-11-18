package fjab

import scala.collection.mutable
import scala.io.Source
import scala.util.Random

//Longest Increasing Subsequence
object LIS extends App {

  def solution1(arr: List[Int]) = {
    val subSequences: mutable.Set[List[Int]] = mutable.Set()

    arr.foreach { x =>
      subSequences ++= (subSequences
        .filter(_.head < x)
        .map(x :: _) match{
        case s if s.isEmpty => Set(List(x))
        case s => s
      })
    }
    subSequences.map(s => (s, s.size)).maxBy(_._2)._1.reverse
  }

  def solution2(arr: List[Int]) = {

    val subSequences: mutable.Set[List[Int]] = mutable.Set()

    arr.foreach { x =>
      subSequences += (subSequences
        .filter(_.head < x)
        .map(l => (l,l.size)) match {
          case s if s.isEmpty => List(x)
          case s => x :: s.maxBy(_._2)._1
        })
    }
    subSequences.map(s => (s, s.size)).maxBy(_._2)._1.reverse
  }

  def solution3(arr: List[Int]) = {

    val subSequences: Array[List[Int]] = new Array(arr.size)
    subSequences(0) = List(arr(0))
    for(i <- Range(1,arr.size))
      subSequences(i) = List(Integer.MAX_VALUE)

    var maxIdx = 0
    for(i <- Range(1,arr.size)){
      var j = maxIdx
      while(j >= 0){
        if(subSequences(j).head < arr(i)) {
          subSequences(j + 1) = arr(i) :: subSequences(j)
          if(j == maxIdx) maxIdx += 1
          j = -1 //to break the loop
        }
        else if(j == 0){
          subSequences(0) = List(arr(i))
          j -= 1
        }
        else
          j -= 1
      }
    }

    subSequences.map(s => (s, s.size)).maxBy(_._2)._1.reverse
  }


  def solution4(arr: List[Int]) = {

    val subSequences: Array[Int] = new Array(arr.size)
    subSequences(0) = arr(0)
    for(i <- Range(1,arr.size))
      subSequences(i) = Integer.MAX_VALUE

    var maxIdx = 0
    for(i <- Range(1,arr.size)){
      var j = maxIdx
      while(j >= 0){
        if(subSequences(j) < arr(i)) {
          subSequences(j + 1) = arr(i)
          if(j == maxIdx) maxIdx += 1
          j = -1
        }
        else if(j == 0){
          subSequences(0) = arr(i)
          j -= 1
        }
        else
          j -= 1
      }
    }
    maxIdx + 1
  }

  def solution5(arr: List[Int]) = {

    def binarySearch(arr: Array[Int], valueSearched: Int, lowerBound: Int, upperBound: Int): Int = {

      if(lowerBound > upperBound) lowerBound - 1
      else {
        val mid = (lowerBound + upperBound) / 2
        if (valueSearched <= arr(mid)) binarySearch(arr, valueSearched, lowerBound, mid - 1)
        else binarySearch(arr, valueSearched, mid + 1, upperBound)
      }
    }

    val subSequences: Array[Int] = new Array(arr.size+1)
    subSequences(0) = Integer.MIN_VALUE
    for(i <- Range(1,arr.size))
      subSequences(i) = Integer.MAX_VALUE

    var maxIdx = 0
    for(i <- Range(1,arr.size)){
      val idx = binarySearch(subSequences, arr(i), 0, maxIdx)
      subSequences(idx + 1) = arr(i)
      if(maxIdx < idx + 1) maxIdx = idx + 1
    }
    maxIdx
  }


  //val array = List(1)
  //val array = List(5,2,7,4,3,8,5,2,7,4,3,8,5,2,7,4,3,8,5,2,7,4,3,8,5,2,7,4,3,8,5,2,7,4,3,8,5,2,7,4,3,8,10,22,9,33,4,3,8,5,2,7,4,3,8,5,2,7,4,3,8,5,2,7,4,3,8,10,22,9,33,4,3,8,5,2,7,4,3,8,5,2,7,4,3,8,5,2,7,4,3,8,10,22,9,33)

  //val array = Source.fromFile("./src/main/resources/testCase1.txt").getLines.toList.map(_.toInt)

  for(i <- Range.inclusive(1,10)) {
    val numElements = 1000*i//Math.pow(5, i).toInt
    val array = (for (j <- Range(0, numElements)) yield Random.nextInt(numElements)).toList

    println("")
    println("**********************************")
    println(s"array length ==> $numElements")
    println("**********************************")

//    {
//      val t = System.currentTimeMillis()
//      println(s"solution1 ${solution1(array).length}")
//      println(s"time1: ${-t + System.currentTimeMillis()}")
//    }

    {
      val t = System.currentTimeMillis()
      println(s"solution2 ${solution2(array).length}")
      println(s"time2: ${-t + System.currentTimeMillis()}")
    }

    {
      val t = System.currentTimeMillis()
      println(s"solution3 ${solution3(array).length}")
      println(s"time3: ${-t + System.currentTimeMillis()}")
    }

    {
      val t = System.currentTimeMillis()
      println(s"solution4 ${solution4(array)}")
      println(s"time4: ${-t + System.currentTimeMillis()}")
    }

    {
      val t = System.currentTimeMillis()
      println(s"solution5 ${solution5(array)}")
      println(s"time5: ${-t + System.currentTimeMillis()}")
    }

  }

}
