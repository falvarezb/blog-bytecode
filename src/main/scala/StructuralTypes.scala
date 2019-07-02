package structural_types

object StructuralTypes extends App {

  //Structural types: take as parameter any type containing 2 int attributes called x and y
  def sumTwoInts(input: {val x: Int; val y: Int}): Int = {
    input.x + input.y
  }

  //Example of types containing 2 ints
  case class Example1(x: Int, y: Int, z: Int)
  object Example2{val x = 3; val z = 2; val y = 5}

  assert(sumTwoInts(Example1(1,2,9)) == 3)
  assert(sumTwoInts(Example2) == 8)


  //Refinement types (extra members are added to a type): particular case of structural types
  def sumTwoInts2(example1: Example1): Example1 {val sum: Int} = {
    new Example1(example1.x, example1.y, example1.z) {val sum = this.x + this.y}
  }

  assert(sumTwoInts2(new Example1(1,2,3)).sum == 3)

}





