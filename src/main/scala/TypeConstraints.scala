package type_constrains


object TypeConstraints extends App {

  /////////// GENERALIZED TYPE CONSTRAINTS
  /*
  <:<, =:=
  These are called generalized type constraints. They allow you, from within a type-parameterized class or trait,
  to further constrain one of its type parameters
   */

  class X[T](a: List[T]){
    def mean(implicit ev: T =:= Int) = a.foldLeft(0)((acc,x) => acc + x)/a.length
  }

  println(new X(List(1,2,3)).mean)
  //println(new X(List("hi")).mean) //won't compile


  ////////// EXISTENTIAL TYPES
  def length[T](xs: List[T]) = xs.length
  println(length(List(1,2,3)))

  def length2(xs: List[_]) = xs.length
  println(length2(List(1,2,3)))
}





