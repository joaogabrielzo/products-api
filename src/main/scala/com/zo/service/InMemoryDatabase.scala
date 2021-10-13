package com.zo.service

import akka.actor.{Actor, ActorLogging}
import com.zo.model.Product

import scala.language.postfixOps

sealed trait Response

sealed trait Event

object InMemoryDatabase {

  case object Done extends Response

  case object Failed extends Response

  case class GetProduct(pred: Product => Boolean)

  case object GetProducts

  case class InsertProduct(id: String, product: Product)

  sealed case class Insert(id: String, product: Product) extends Event
}

class InMemoryDatabase extends Actor with ActorLogging {

  import InMemoryDatabase._

  var db = Map[String, Product]()

  override def receive: Receive = {
    case GetProduct(f) =>
      sender() ! db.filter(p => f(p._2)).values.toList

    case GetProducts =>
      sender() ! db.values.toList

    case InsertProduct(id, p) => {
      if (db.contains(id))
        sender() ! Failed
      else {
        eventHandler(Insert(id, p))
        sender() ! Done
      }
    }
  }

  def eventHandler(r: Event): Unit = r match {
    case Insert(id, p) =>
      db += (id -> p)
  }
}