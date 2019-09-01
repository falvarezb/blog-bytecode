List(1,2,3,4).reduceLeft((acc,x) => acc - x)

List(1,2,3,4).reduceRight((x, acc) => x - acc)

List(1,2,3,4).reduce((x, acc) => x - acc)

List(1,2,3,4).foldLeft(0)((acc,x) => acc - x)

List(1,2,3,4).foldRight(0)((x, acc) => x - acc)

def sumLoop(xs: List[Int]): Int = {
  var acc = 0
  for(x <- xs){
    acc += x
  }
  acc
}


def sumRecursive(xs: List[Int]): Int = xs match {
  case Nil => 0
  case x :: tail => x + sumRecursive(tail)
}


def sumFold(xs: List[Int]): Int = xs.reduce((acc, x) => acc + x)

val xs = List(1,2,3,4)

sumLoop(xs)
sumRecursive(xs)
sumFold(xs)

case class Rectangle(side1: Int, side2: Int)
def area(rectangle: Rectangle): Int = rectangle.side1 * rectangle.side2
area(Rectangle(2,3))