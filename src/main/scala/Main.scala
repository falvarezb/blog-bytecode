import io.circe.Json
import io.circe.optics.all.jsonString
import io.circe.parser._

object Main {

  def main(args: Array[String]): Unit = {
    jsonMerge()
  }

  def jsonMerge(): Unit = {
    val jsonString1 = """{
                       |      "data": {
                       |        "market": "UK",
                       |        "company": "EHL",
                       |        "currency": "GBP",
                       |        "amount": 13566
                       |      }
                       |    }""".stripMargin

    val jsonString2 = """{
                        |        "paymentReference": "xx_yy",
                        |        "ipAddress": "127.0"
                        |    }""".stripMargin

    val jsonData1 = parse(jsonString1).right.get
    val jsonData2 = parse(jsonString2).right.get

    val merged = jsonData1.deepMerge(jsonData2)
    println(merged.noSpaces)
  }

}
