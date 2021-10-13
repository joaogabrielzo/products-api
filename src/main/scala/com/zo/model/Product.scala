package com.zo.model

import org.joda.time.DateTime
import org.joda.time.format.{DateTimeFormat, DateTimeFormatter}
import spray.json.{DefaultJsonProtocol, JsString, JsValue, RootJsonFormat}


case class Product(
                    name: String,
                    vendor: String,
                    price: Double,
                    expirationDate: Option[DateTime]
                  )

trait JsonProtocol extends DefaultJsonProtocol {

  implicit val dateMarshalling: DateJsonFormat.type = DateJsonFormat

  implicit val productFormat: RootJsonFormat[Product] = jsonFormat4(Product)
}

object DateJsonFormat extends RootJsonFormat[DateTime] {

  private val dateTimePattern = "yyyy-MM-dd";

  val dateTimeFormatter: DateTimeFormatter = DateTimeFormat.forPattern(dateTimePattern)

  override def write(obj: DateTime): JsString = JsString(obj.toString(dateTimePattern))

  override def read(json: JsValue): DateTime = json match {
    case JsString(s) => dateTimeFormatter.parseDateTime(s)
  }
}
