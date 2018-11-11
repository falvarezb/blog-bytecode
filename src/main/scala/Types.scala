object Types extends App {

  //Types are sets of values

  //Empty set, type without any value, represents non-terminating computations
  //This type is called 'Nothing' in Scala and is a sub-type of all other types

  /*
      This function can be invoked but never returns anything
      This return type allows the compiler to know that a function will never
      terminate normally (without needing to execute it)
   */
  def nonTerminating(): Nothing = throw new RuntimeException

  //nonTerminating()


  /*
      Absurd function as it can be declared but cannot be invoked as there is no value
      of type Nothing to pass as a parameter in the function invocation
   */
  def absurd(x: Nothing): Int = 1

  //This statement does not compile
  absurd()

  val unit: Unit => Unit = x => Unit

  println(unit())
  println(unit(Unit))

  def unitMethod(x:Unit): Unit = Unit
  println(unitMethod())
  println(unitMethod(Unit))

  val noparams: () => String = () => "hello"
}
