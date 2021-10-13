package com.zo

import akka.actor.{ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit}
import com.zo.model.Product
import com.zo.service.InMemoryDatabase
import com.zo.service.InMemoryDatabase.{Done, Failed, GetProduct, GetProducts, InsertProduct}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

class InMemoryDatabaseTests extends TestKit(ActorSystem("database-test"))
  with AnyWordSpecLike
  with Matchers
  with ImplicitSender {

  "an In Memory Database actor" should {
      val dbActor = system.actorOf(Props[InMemoryDatabase])
      val productToSave = Product("Macbook", "Apple", 5.5, None)
      val anotherProductToSave = Product("Laptop", "Dell", 1.2, None)

    "return Done when a product is successfully saved" in {
      dbActor ! InsertProduct("1", productToSave)

      expectMsg(Done)
    }

    "return Failed when trying to save a Product with duplicated ID" in {
      dbActor ! InsertProduct("1", productToSave)

      expectMsg(Failed)
    }

    "return a List of Products when queried by Vendor" in {
      dbActor ! GetProduct((x: Product) => x.vendor == "Apple")

      expectMsg(List(productToSave))
    }

    "return a List with only the Products with price greater than requests" in {
      dbActor ! InsertProduct("2", anotherProductToSave)

      expectMsg(Done)

      dbActor ! GetProduct((x: Product) => x.price > 3)

      expectMsg(List(productToSave))
    }

    "return a List with all the Products" in {
      dbActor ! GetProducts

      expectMsg(List(productToSave, anotherProductToSave))
    }
  }

}
