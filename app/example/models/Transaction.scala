package example.models

import play.api.libs.json._

case class Transaction(
  CustomerID: String,
  Description: String,
  UnitPrice: Double,
  InvoiceDate: String
)

object Transaction {
  implicit val format: OFormat[Transaction] = Json.format[Transaction]
}
