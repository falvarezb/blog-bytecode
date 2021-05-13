package object fjab {

  case class Address(street: String, postCode: String, country: String)
  case class Hotel(name: String, checkinDate: String, checkoutDate: String)
  case class Flight(flightNumber: String, departureTime: String, arrivalTime: String)
  case class Passenger(firstName: String, lastName: String, age: Int, address: Address)
  case class Travel(passengers: List[Passenger], hotel: Option[Hotel], flight: Option[Flight])
}
