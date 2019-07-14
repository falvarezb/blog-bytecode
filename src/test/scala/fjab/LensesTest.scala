package fjab

import fjab.Hotel._
import fjab.Price._
import fjab.Room._
import monocle._
import monocle.function.Field3
import monocle.function.all._
import monocle.law.discipline.PrismTests
import monocle.std.option.some
import monocle.unsafe.UnsafeSelect
import org.scalacheck.{Arbitrary, Gen}
import org.scalatest.FunSuite
import org.typelevel.discipline.scalatest.Discipline
import scalaz.Equal
import scalaz.Scalaz._

import scala.util.Try

class LensesTest extends FunSuite with Discipline {

  val rooms = List(
    Room("Double", Some("Half Board"), Price(10, "USD"), NonRefundable(1)),
    Room("Twin", None, Price(20, "USD"), Flexible(0)) ,
    Room("Executive", None, Price(200, "USD"), Flexible(0))
  )
  val facilities = Map("business" -> List("conference room"))
  val hotel = Hotel("Hotel Paradise", "100 High Street", 5, rooms, facilities)

  test("double price of even rooms") {

    val updatedHotel = (_rooms composeTraversal filterIndex{i: Int => i/2*2 == i} composeLens _price composeLens _amount modify(_ * 2)) (hotel)

    assert(updatedHotel.rooms(0).price.amount == hotel.rooms(0).price.amount * 2)
    assert(updatedHotel.rooms(1).price.amount == hotel.rooms(1).price.amount)
    assert(updatedHotel.rooms(2).price.amount == hotel.rooms(2).price.amount * 2)
  }

  test("set price of 2nd room") {

    val newValue = 12
    val roomToUpdate = 1

    assert(hotel.rooms(roomToUpdate).price.amount != newValue)

    val updatedHotel = (_rooms composeOptional index(roomToUpdate) composeLens _price composeLens _amount set newValue)(hotel)
    val updatedRoomList = (index[List[Room], Int, Room](roomToUpdate) composeLens _price composeLens _amount set newValue)(hotel.rooms)

    assert(updatedHotel.rooms(roomToUpdate).price.amount == newValue)
    assert(updatedRoomList(roomToUpdate).price.amount == newValue)
  }

  test("no changes are made when attempting to modify a non-existing room") {

    val newValue = 12
    val roomToUpdate = 3

    assert(hotel.rooms.length == 3)

    val updatedHotel = (_rooms composeOptional index(roomToUpdate) composeLens _price composeLens _amount set newValue)(hotel)

    assert(hotel == updatedHotel)
  }

  test("hotel 'disappears' when attempting to modify a non-existing room") {

    val newValue = 12
    val roomToUpdate = 3

    assert(hotel.rooms.length == 3)

    val updatedHotel = (_rooms composeOptional index(roomToUpdate) composeLens _price composeLens _amount setOption newValue)(hotel)

    assert(updatedHotel.isEmpty)
  }

  test("set a value inside an Option") {

    val newValue = "New Board Type"
    val roomToUpdate = 0

    assert(!hotel.rooms(roomToUpdate).boardType.contains(newValue))

    val updatedHotel = (_rooms composeOptional index(roomToUpdate) composeLens _boardType composeOptional some.asOptional set newValue)(hotel)

    assert(updatedHotel.rooms(roomToUpdate).boardType.contains(newValue))
  }

  test("no changes are made when attempting to modify an empty Option") {

    val newValue = "New Board Type"
    val roomToUpdate = 1

    assert(hotel.rooms(roomToUpdate).boardType.isEmpty)

    val updatedHotel = (_rooms composeOptional index(roomToUpdate) composeLens _boardType composeOptional some.asOptional set newValue)(hotel)

    assert(updatedHotel.rooms(roomToUpdate).boardType.isEmpty)
  }

  test("hotel 'disappears' when attempting to modify an empty Option") {

    val newValue = "New Board Type"
    val roomToUpdate = 1

    assert(hotel.rooms(roomToUpdate).boardType.isEmpty)

    val updatedHotel = (_rooms composeOptional index(roomToUpdate) composeLens _boardType composeOptional some.asOptional setOption newValue)(hotel)

    assert(updatedHotel.isEmpty)
  }

  test("folding over room prices to add them up") {

    assert(hotel.rooms(0).price.amount == 10)
    assert(hotel.rooms(1).price.amount == 20)
    assert(hotel.rooms(2).price.amount == 200)

    assert((_rooms composeFold Fold.fromFoldable[List, Room] foldMap(_.price.amount))(hotel) == 230)
  }

  test("divide prices by 10"){

    assert(hotel.rooms(0).price.amount == 10)
    assert(hotel.rooms(1).price.amount == 20)

    val updatedHotel = (_rooms composeTraversal each composeLens _price composeLens _amount modify(_ / 10))(hotel)

    assert(updatedHotel.rooms(0).price.amount == 1)
    assert(updatedHotel.rooms(1).price.amount == 2)
  }

  test("divide prices by 0"){

    assert(hotel.rooms(0).price.amount == 10)
    assert(hotel.rooms(1).price.amount == 20)

    val updatedHotel = (_rooms composeTraversal each composeLens _price composeLens _amount).modifyF[Option](y => Try{y / 0}.toOption)(hotel)

    assert(updatedHotel.isEmpty)
  }

  test("append a room"){

    assert(hotel.rooms.length == 3)

    val newRoom = Room("Triple", None, Price(1, "USD"), Flexible(0))

    val updatedHotel = (_rooms set _snoc(hotel.rooms, newRoom))(hotel)

    assert(updatedHotel.rooms.length == 4)
    assert(updatedHotel.rooms(3) == newRoom)
  }

  test("prepend a room"){

    assert(hotel.rooms.length == 3)

    val newRoom = Room("Triple", None, Price(1, "USD"), Flexible(0))

    val updatedHotel = (_rooms set _cons(newRoom, hotel.rooms))(hotel)

    assert(updatedHotel.rooms.length == 4)
    assert(updatedHotel.rooms(0) == newRoom)
  }

  test("set prices of Flexible rooms"){

    val prism = Prism.partial[RoomTariff, BigDecimal]{case Flexible(x) => x}(Flexible)

    val newValue = 100

    assert(hotel.rooms(0).roomTariff == NonRefundable(1))
    assert(hotel.rooms(1).roomTariff == Flexible(0))
    assert(hotel.rooms(2).roomTariff == Flexible(0))

    val updatedHotel = (_rooms composeTraversal each composeLens _roomTariff composePrism prism set newValue)(hotel)

    assert(hotel.rooms(0).roomTariff == updatedHotel.rooms(0).roomTariff)
    assert(updatedHotel.rooms(1).roomTariff == Flexible(newValue))
    assert(updatedHotel.rooms(2).roomTariff == Flexible(newValue))
  }

  test("modifying business facilities") {

    val updatedHotel = (_facilities composeLens at("business") set Some(List("")))(hotel)

    assert(updatedHotel.facilities("business") == List(""))
  }

  test("removing business facilities") {

    val updatedHotel = (_facilities composeLens at("business") set None)(hotel)
    val updatedFacilities = remove("business")(hotel.facilities)

    assert(updatedHotel.facilities.get("business").isEmpty)
    assert(updatedFacilities.get("business").isEmpty)
  }

  test("adding entertainment facilities") {

    val updatedHotel = (_facilities composeLens at("entertainment") set  Some(List("satellite tv", "internet")))(hotel)

    assert(updatedHotel.facilities("entertainment") == List("satellite tv", "internet"))
  }

  test("setting 1st element of tuple") {

    val result = (first[(Int, Int), Int] set 1)((3,4))

    assert(result == ((1,4)))
  }

  test("setting 3rd element of List") {

    implicit val ev = new Field3[List[Int], Int]{
      override def third: Lens[List[Int], Int] = Lens[List[Int], Int] (_(2))((a: Int) => (t: List[Int]) => t.take(2) ::: List(a) ::: t.drop(3))
    }

    val result = (third[List[Int], Int] set 1)(List(3,4,5))

    assert(result == List(3,4,1))
  }

  val unsafePrism = UnsafeSelect.unsafeSelect[Room](_.name == "Double")
  test("double price of Double rooms using unsafe operation") {

    val updatedHotel = (_rooms composeTraversal each composePrism unsafePrism composeLens _price composeLens _amount modify (_ * 2)) (hotel)

    assert(hotel.rooms.filter(_.name == "Double").map(_.price.amount*2) == updatedHotel.rooms.filter(_.name == "Double").map(_.price.amount))
  }

  implicit val roomEqual: Equal[Room] = Equal.equalA[Room]

  val roomGen: Gen[Room] = for {
    name <- Gen.oneOf("Double", "Twin", "Executive")
    board <- Gen.option(Gen.alphaStr)
    price <- for{
      price <- Gen.posNum[Double]
      currency <- Gen.oneOf("USD", "GBP", "EUR")
    } yield Price(price, currency)
    tariff <- Gen.oneOf(Gen.posNum[Double].map(NonRefundable(_)), Gen.posNum[Double].map(Flexible(_)))
  } yield Room(name, board, price, tariff)

  implicit val roomArb: Arbitrary[Room] = Arbitrary(roomGen)

  implicit val arbAA: Arbitrary[Room => Room] = Arbitrary{
    for{
      room <- roomGen
    } yield (_: Room) => room
  }

  checkAll("unsafe prism", PrismTests(unsafePrism))


  val anotherUnlawfulPrism = Prism[Int, Double]
    {s => Try(math.sqrt(s)).toOption}
    {b => (b * b) toInt}

  test("another unlawful prism example") {

    2 match {
      case anotherUnlawfulPrism(y) => assert(y == 1.4142135623730951)
      case _ => fail()
    }

    -2 match {
      case anotherUnlawfulPrism(y) => assert(y.isNaN)
      case _ => fail()
    }
  }

  checkAll("prism laws fail for negative numbers", PrismTests(anotherUnlawfulPrism))

}
