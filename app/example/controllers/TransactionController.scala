package example.controllers

import play.api.mvc._
import play.api.libs.json._
import redis.clients.jedis.Jedis
import javax.inject._
import example.RedisConfig
import example.models.Transaction
import scala.jdk.CollectionConverters._
@Singleton
class TransactionController @Inject()(val controllerComponents: ControllerComponents)
  extends BaseController {

  private val jedis = new Jedis(RedisConfig.host, RedisConfig.port)
  if (RedisConfig.password.nonEmpty) jedis.auth(RedisConfig.password)

  def getTransactions(customerId: String) = Action {
    val keys =
      if (customerId.contains(":")) {
        // Caso: clave completa (ej. customer:15745:2011-09-27)
        jedis.keys(s"sept2011_purchases:$customerId")
      } else {
        // Caso: solo ID de cliente (ej. 15745)
        jedis.keys(s"sept2011_purchases:customer:$customerId:*")
      }

    if (keys.isEmpty) {
      NotFound(Json.obj("error" -> "Cliente no encontrado o sin transacciones"))
    } else {
      val transactions = keys.toArray.collect {
        case key: String =>
          val data = jedis.hgetAll(key)
          Transaction(
            CustomerID = customerId,
            Description = data.getOrDefault("Description", ""),
            UnitPrice = data.getOrDefault("UnitPrice", "0").toDouble,
            InvoiceDate = data.getOrDefault("InvoiceDate", "")
          )
      }.toList

      Ok(Json.toJson(transactions))
    }
  }

  def addTransaction = Action(parse.json) { request =>
    request.body.validate[Transaction] match {
      case JsSuccess(transaction, _) =>
        // Clave con el formato usado en Spark
        val key =
          s"sept2011_purchases:customer:${transaction.CustomerID}:${transaction.InvoiceDate}"

        // Guardar como hash en Redis
        jedis.hset(key, Map(
          "CustomerID"  -> transaction.CustomerID,
          "Description" -> transaction.Description,
          "UnitPrice"   -> transaction.UnitPrice.toString,
          "InvoiceDate" -> transaction.InvoiceDate
        ).asJava)

        Created(Json.obj("message" -> s"Transacción guardada con clave $key"))

      case JsError(errors) =>
        BadRequest(Json.obj("error" -> "JSON inválido", "details" -> JsError.toJson(errors)))
    }
  }

  def notFound(path: String) = Action {
    NotFound(Json.obj(
      "error" -> s"Endpoint '$path' not found",
      "status" -> 404
    ))
  }

}
