package fjabs

import scala.reflect.runtime.universe

object TypeTagsExamples extends App {

  import scala.reflect.runtime.universe._
  val tt: universe.TypeTag[Int] = typeTag[Int]
  println(tt.toString())
  tt.tpe

  import scala.reflect._
  val ct: ClassTag[String] = classTag[String]
  println(ct.toString())

  def paramInfo[T](x: T)(implicit tag: TypeTag[T]): Unit = {
    val targs = tag.tpe match {
      case TypeRef(pre, sym, args) => println(s"pre=$pre sym=$sym args=$args tag=$tag");args
    }
    println(s"type of $x has type arguments $targs")
  }

  println(paramInfo(2))
  println(paramInfo(Some("33")))

  def extract[T: ClassTag](list: Seq[Any]) =
    list.flatMap {
      case element: T => Some(element)
      case _ => None
    }

  val list: Seq[Any] = Seq(1, "string1", Seq(), "string2", Seq(1,2), Seq("2"))
  val result = extract[Seq[String]](list)
  println(result) // List(string1, string2)

  def extract2[T](list: Seq[Any])(implicit ttag: TypeTag[T]) =
    list.flatMap {
      case element: Seq[Int] if (typeOf[T] =:= typeOf[Seq[Int]]) => {
         Some(element)
      }
      case _ => None
    }

  val result2 = extract2[Seq[Int]](list)
  println(result2) // List(string1, string2)

  case class Thing[T](value: T)

  def processThing[T : TypeTag](thing: Thing[T]) = {
    thing match {
      case Thing(value: Int) => "Thing of int " + value.toString
      case Thing(value: Seq[Int]) if typeOf[T] =:= typeOf[Seq[Int]] => "Thing of seq of int" + value.sum
      case _ => "Thing of something else"
    }
  }

  println(processThing(Thing(Seq(1,2,3))))
  println(processThing(Thing(Seq("hello", "yo"))))

}
