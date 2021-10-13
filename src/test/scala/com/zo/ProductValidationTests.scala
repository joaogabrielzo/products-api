package com.zo

import com.zo.model.{Product, ProductValidator}
import org.joda.time.DateTime
import org.joda.time.format.{DateTimeFormat, DateTimeFormatter}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class ProductValidationTests extends AnyWordSpec
  with Matchers {

  private val dateTimePattern = "dd-MM-yyyy";
  private val dateTimeFormatter: DateTimeFormatter = DateTimeFormat.forPattern(dateTimePattern)

  "Product case class validation" should {
    "return an empty List when validation passes" in {
      val product = Product("Macbook", "Apple", 8000.00, None)
      val validation = ProductValidator(product)

      assertResult(List())(validation)
    }

    "return a non empty List when validation fails" in {
      val nameTooLongProduct = Product("SuperLongName", "Apple", 8000.00, Some(DateTime.parse("11-11-2022", dateTimeFormatter)))
      val nameValidation = ProductValidator(nameTooLongProduct)

      assertResult("Product name too long.")(nameValidation.head)

      val vendorTooLongProduct = Product("Macbook", "PoisonedApple", 8000.00, Some(DateTime.parse("11-11-2022", dateTimeFormatter)))
      val vendorValidation = ProductValidator(vendorTooLongProduct)

      assertResult("Vendor name too long.")(vendorValidation.head)

      val negativePriceProduct = Product("Macbook", "Apple", -5, Some(DateTime.parse("11-11-2022", dateTimeFormatter)))
      val priceValidation = ProductValidator(negativePriceProduct)

      assertResult("Price cannot be negative.")(priceValidation.head)

      val pastDateProduct = Product("Macbook", "Apple", 8000.00, Some(DateTime.parse("11-11-2018", dateTimeFormatter)))
      val dateValidation = ProductValidator(pastDateProduct)

      assertResult("Expiration Date must be in the future.")(dateValidation.head)
    }

    "return a List with 4 objects when all validations fail" in {
      val superBadProduct = Product("SuperLongName", "SuperLongApple", 0, Some(DateTime.parse("11-11-2017", dateTimeFormatter)))
      val badValidation = ProductValidator(superBadProduct)

      assertResult(4)(badValidation.size)
    }
  }
}
