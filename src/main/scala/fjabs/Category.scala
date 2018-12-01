package fjabs

object Category extends App {

  val x = 4

  //Composition
  val f: Int => Int = _*2
  val g: Int => Int = _+1
  assert((f andThen g)(x) == 9)


  //Properties of composition
  //1.associative
  val h: Int => Int = _+2
  assert(((f andThen g) andThen h)(x) == (f andThen (g andThen h))(x))

  //2.identity
  def id[T](t:T):T = t
  assert((f andThen id)(x) == (id[Int]_ andThen f)(x))

  //(Scala already has a predefined identity function)
  assert(identity(x) == x)

  def myMethod(x: Int) = x
  val functionDerivedFromMethod = myMethod _
  assert(functionDerivedFromMethod(x) == x)

}
