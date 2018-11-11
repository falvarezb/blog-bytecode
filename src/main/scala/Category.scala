object Category extends App {

  //Composition
  val f: Int => Int = _*2
  val g: Int => Int = _+1
  def `◦`[R,S,T](f: R => S, g: S => T): (R => T) = (x:R) => g(f(x))
  assert(`◦`(f,g)(4) == 9)

  //Properties of composition
  //1.associative
  val h: Int => Int = _+2
  assert(`◦`(`◦`(f,g), h)(4) ==  `◦`(f, `◦`(g,h))(4))

  //2.identity
  def id[T](t:T):T = t
  assert(`◦`(id[Int],f)(4) == f(4))
  assert(`◦`(id[Int],f)(4) == `◦`(f,id[Int])(4))

  //(Scala already has a predefined identity function)
  assert(identity("fe") == "fe")

}
