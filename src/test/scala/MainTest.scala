import com.softwaremill.diffx.Diff
import com.softwaremill.diffx.scalatest.DiffMatcher
import org.scalatest.Matchers
import org.scalatest.flatspec.{AnyFlatSpec, AnyFlatSpecLike}
import io.circe.Json
import io.circe.parser._

class MainTest extends AnyFlatSpec with Matchers with DiffMatcher {

  implicit val jsonDiff: Diff[Json] = Diff[String].contramap[Json](_.noSpaces)
  val stringifiedJson = """{"foo":"bar","baz":123,"list":[4,5,6]}"""
  val jsonData: Json = parse(stringifiedJson).right.get
  implicit val adyenDiff = Diff.derived[AdyenData]
  implicit val paymentDiff = Diff.derived[Payment]


  "Test" should "pass" in new {
    assert(true)
    Payment(AdyenData(Some(jsonData), None)) should matchTo(Payment(AdyenData(Some(jsonData), None)))
  }

}
