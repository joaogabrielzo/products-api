package com.zo.validation

trait Validator[T] extends (T => Seq[String]) {

  protected def validationStage(rule: Boolean, errorText: String): Option[String] =
    if (rule) Some(errorText) else None

}
