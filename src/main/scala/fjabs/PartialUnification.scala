package fjabs

import scala.reflect.runtime.universe._
object PartialUnification extends App {

  def show[F[_], A: TypeTag](f: F[A])(implicit tt: TypeTag[F[A]]) = {
    println(s"${typeOf[F[A]]} and A: ${typeOf[A]}")
  }

  show(List(1, 2, 3))
  show(Some("hello"))

  val err: Either[Error, String] = Left(new Error("boom"))
  show(err)


}
