package fjabs

import cats._
import cats.implicits._

object AlternativeExample extends App {

  trait Decoder[A] {
    def decode(in: String): Either[Throwable, A]
  }
  object Decoder {
    def from[A](f: String => Either[Throwable, A]): Decoder[A] = in => f(in)
  }

  implicit val decoderAlternative: Alternative[Decoder] = new Alternative[Decoder] {
    def pure[A](a: A) = Decoder.from(_ => Right(a))

    def empty[A] = Decoder.from(_ => Left(new Error("No dice.")))

    def combineK[A](l: Decoder[A], r: Decoder[A]): Decoder[A] = in => l.decode(in).orElse(r.decode(in))

    def ap[A, B](ff: Decoder[A => B])(fa: Decoder[A]): Decoder[B] = in => fa.decode(in) ap ff.decode(in)
  }

  //laws
  //Right absorption
  val ff: Decoder[Int => Double] = decoderAlternative.pure(i => (i*3).toDouble)
  val testArg = "hello"
  assert((ff <*> decoderAlternative.empty[Int]).decode(testArg).left.get.getMessage == decoderAlternative.empty[Double].decode(testArg).left.get.getMessage)

  //Left Distributivity
  val fa = 1.pure[Decoder]
  val fb = 2.pure[Decoder]
  val f: Int => Double = _.toDouble

  assert(((fa <+> fb) map f).decode(testArg) == ((fa map f) <+> (fb map f)).decode(testArg))

  //Right Distributivity
  val gg: Decoder[Int => Double] = decoderAlternative.pure(i => (i*2).toDouble)

  assert(((ff <+> gg) <*> fa).decode(testArg) == ((ff <*> fa) <+> (gg <*> fa)).decode(testArg))


  //Examples

  def parseInt(s: String): Either[Throwable, Int] = Either.catchNonFatal(s.toInt)
  def parseIntFirstChar(s: String): Either[Throwable, Int] = Either.catchNonFatal(2 * Character.digit(s.charAt(0), 10))

  // Try first parsing the whole, then just the first character.
  val decoder: Decoder[Int] = Decoder.from(parseInt) <+> Decoder.from(parseIntFirstChar)

  println(decoder.decode("555"))
  // res1: Either[Throwable,Int] = Right(555)

  println(decoder.decode("5a"))
  // res2: Either[Throwable,Int] = Right(10)

  val decoder2: Decoder[Double] = decoderAlternative.ap[Int, Double](Decoder.from(str => Right((i: Int) => i + str.length.toDouble)))(Decoder.from(parseInt _))

  println(decoder2.decode("10"))

  println(List("1","4kl","hello","32").map(decoder.decode).separate)
  println(List("1","4kl","hello","32").map(decoder.decode).unite)



  val empty = Alternative[Vector].empty[Int]
  // empty: Vector[Int] = Vector()

  val pureOfFive = 5.pure[Vector]
  // pureOfFive: Vector[Int] = Vector(5)

  val concatenated: Vector[Int] = 7.pure[Vector] <+> 8.pure[Vector]
  // concatenated: Vector[Int] = Vector(7, 8)

  val double: Int => Int = _ * 2
  // double: Int => Int = $$Lambda$14390/251393288@72315e99

  val addFive: Int => Int = _ + 5
  // addFive: Int => Int = $$Lambda$14391/1385519241@780765b3

  val apForVectors = (double.pure[Vector] <+> addFive.pure[Vector]) <*> concatenated
  // apForVectors: Vector[Int] = Vector(14, 16, 12, 13)
  println(apForVectors)

  val apForVectorsVerbose = Alternative[Vector].ap(Alternative[Vector].combineK(Alternative[Vector].pure(double), Alternative[Vector].pure(addFive)))(concatenated)
  println(apForVectorsVerbose)


}
