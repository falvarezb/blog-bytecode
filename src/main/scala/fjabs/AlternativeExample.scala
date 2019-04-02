package fjabs

import cats._
import cats.implicits._

object AlternativeExample extends App {

  trait Decoder[A] {
    def decode(in: String): Either[Throwable, A]
  }
  object Decoder {
    def from[A](f: String => Either[Throwable, A]): Decoder[A] =
      (in: String) => f(in)
  }

  implicit val decoderAlternative: Alternative[Decoder] = new Alternative[Decoder] {
    def pure[A](a: A) = Decoder.from(Function.const(Right(a)))

    def empty[A] = Decoder.from(Function.const(Left(new Error("No dice."))))

    def combineK[A](l: Decoder[A], r: Decoder[A]): Decoder[A] =
      (in: String) => l.decode(in).orElse(r.decode(in))

    def ap[A, B](ff: Decoder[A => B])(fa: Decoder[A]): Decoder[B] =
      (in: String) => fa.decode(in).ap(ff.decode(in))
  }

  def parseInt(s: String): Either[Throwable, Int] = Either.catchNonFatal(s.toInt)
  def parseIntFirstChar(s: String): Either[Throwable, Int] = Either.catchNonFatal(2 * Character.digit(s.charAt(0), 10))

  // Try first parsing the whole, then just the first character.
  val decoder: Decoder[Int] = Decoder.from(parseInt _) <+> Decoder.from(parseIntFirstChar _)

  println(decoder.decode("555"))
  // res1: Either[Throwable,Int] = Right(555)

  println(decoder.decode("5a"))
  // res2: Either[Throwable,Int] = Right(10)

  val decoder2: Decoder[Double] = decoderAlternative.ap[Int, Double](Decoder.from(str => Right((i: Int) => i + str.length.toDouble)))(Decoder.from(parseInt _))

  println(decoder2.decode("10"))

}
