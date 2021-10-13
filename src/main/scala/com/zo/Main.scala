package com.zo

import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.Http
import com.zo.controller.ProductsEndpoint
import com.zo.service.InMemoryDatabase

object Main extends App {

  implicit val system = ActorSystem()
  implicit val databaseActor = system.actorOf(Props[InMemoryDatabase])
  implicit val ec = system.dispatcher

  val productsRoute = new ProductsEndpoint().route

  Http().newServerAt("localhost", 8080).bind(productsRoute)


}
