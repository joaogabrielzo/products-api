package com.zo.controller

import akka.actor.ActorRef
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.{HttpEntity, HttpResponse}
import akka.http.scaladsl.model.StatusCodes.{BadRequest, Created, OK}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import akka.util.Timeout
import com.zo.controller.handler.ErrorHandler.{exceptionHandler, rejectionHandler}
import com.zo.model.{JsonProtocol, Product, ProductValidator}
import com.zo.service.InMemoryDatabase._
import com.zo.service.Response
import com.zo.validation.ValidationRules.validateModel

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

class ProductsEndpoint(implicit ec: ExecutionContext, dbActor: ActorRef) extends SprayJsonSupport with JsonProtocol {

  implicit val timeout: Timeout = akka.util.Timeout(5 seconds)
  implicit val productValidator = ProductValidator

  val route: Route =
    get {
      (path("products") & parameter("vendor")) { vendor: String =>
        complete {
          (dbActor ? GetProduct((x: Product) => x.vendor == vendor)).mapTo[List[Product]]
        }
      }
    } ~
      handleExceptions(exceptionHandler) {
        get {
          (path("products") & parameter("priceGT")) { price: String =>
            val convertedPrice = price.toDouble
            complete(
              (dbActor ? GetProduct((x: Product) => x.price > convertedPrice)).mapTo[List[Product]]
            )
          }
        }
      } ~
      get {
        path("products") {
          complete(
            (dbActor ? GetProducts).mapTo[List[Product]]
          )
        }
      } ~
      handleRejections(rejectionHandler) {
        post {
          path("products" / Segment) { id: String =>
            entity(as[Product]) { product =>
              validateModel(product).apply { validatedProduct =>
                complete {
                  (dbActor ? InsertProduct(id, validatedProduct)).mapTo[Response].map {
                    case Done => HttpResponse(Created, entity = HttpEntity("Product registered."))
                    case Failed => HttpResponse(BadRequest, entity = HttpEntity("Product ID is duplicated."))
                  }
                }
              }
            }
          }
        }
      }
}
