import io.circe.Json

object Main {

}

case class AdyenData(mainData: Option[Json], additionalDetails: Option[Json])
case class Payment(data: AdyenData)
