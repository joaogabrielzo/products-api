package com.zo.controller.handler

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCode
import akka.http.scaladsl.model.StatusCodes.BadRequest
import akka.http.scaladsl.server.Directives.complete
import akka.http.scaladsl.server.{ExceptionHandler, RejectionHandler}
import com.zo.model.JsonProtocol
import com.zo.validation.ValidationRules.ModelValidationRejection

object ErrorHandler extends JsonProtocol with SprayJsonSupport {


  def exceptionHandler: ExceptionHandler =
    ExceptionHandler {
      case _: NumberFormatException =>
        complete(BadRequest, "Price should be a number.")
    }

  def rejectionHandler: RejectionHandler =
    RejectionHandler
      .newBuilder()
      .handle {
        case ModelValidationRejection(invalidFields) =>
          complete(StatusCode.int2StatusCode(400), invalidFields)
      }.result()

}
