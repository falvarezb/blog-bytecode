package fjab

import monocle.macros.Lenses

sealed trait RoomTariff
case class NonRefundable(fee: BigDecimal) extends RoomTariff
case class Flexible(fee: BigDecimal) extends RoomTariff


@Lenses("_") case class Hotel(name: String, address: String, rating: Int, rooms: List[Room], facilities: Map[String, List[String]])
@Lenses("_") case class Room(name: String, boardType: Option[String], price: Price, roomTariff: RoomTariff)
@Lenses("_") case class Price(amount: BigDecimal, currency: String)
