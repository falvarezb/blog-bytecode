object Example {

  /*
    Scala assertions are resolved at compile time whereas Java assertions are resolved at runtime

    Scala assertions are enabled by default
    Java assertions are disabled by default

    scalac option to disable assertions: -Xdisable-assertions
    java JVM option to enable assertions: -ea
   */
  def main(args: Array[String]): Unit = {
    println("hello world")
  }

  def example(): Boolean = {
    println("hello world")
    true
  }

}
