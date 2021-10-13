package com.zo.model

import com.zo.validation.Validator
import org.joda.time.DateTime

object ProductValidator extends Validator[Product] {

  private def nameTooLongRule(name: String): Boolean = name.length > 10

  private def priceNegativeRule(price: Double): Boolean = price <= 0

  private def expirationDateIsPast(date: DateTime): Boolean = date.isEqualNow || date.isBeforeNow

  override def apply(product: Product): List[String] = {
    val productNameOpt = validationStage(nameTooLongRule(product.name), "Product name too long.")
    val vendorNameOpt = validationStage(nameTooLongRule(product.vendor), "Vendor name too long.")
    val priceOpt = validationStage(priceNegativeRule(product.price), "Price cannot be negative.")
    val expirationDateOpt =
      if (product.expirationDate.isDefined)
        validationStage(expirationDateIsPast(product.expirationDate.get), "Expiration Date must be in the future.")
      else None

    List(productNameOpt, vendorNameOpt, priceOpt, expirationDateOpt).flatten
  }
}
