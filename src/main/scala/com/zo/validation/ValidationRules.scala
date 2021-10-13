package com.zo.validation

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Directives.{provide, reject}
import akka.http.scaladsl.server.{Directive1, Rejection}
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

object ValidationRules extends SprayJsonSupport with DefaultJsonProtocol {

  final case class FieldErrors(errors: List[String])

  implicit val jsonConstraintsFormat: RootJsonFormat[FieldErrors] = jsonFormat1(FieldErrors)

  final case class ModelValidationRejection(invalidFields: FieldErrors) extends Rejection

  def validateModel[T](model: T)(implicit validator: Validator[T]): Directive1[T] = {
    validator(model) match {
      case Nil => provide(model)
      case errors: List[String] => reject(ModelValidationRejection(FieldErrors(errors)))
    }
  }

}
