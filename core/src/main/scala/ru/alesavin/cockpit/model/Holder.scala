package ru.alesavin.cockpit.model

/**
  * TODO
  *
  * @author alesavin
  */
final case class Holder(`type`: String,
                        value: String) {

  require(Option(`type`).nonEmpty && `type`.nonEmpty, "Type is null or empty")
  require(Option(value).nonEmpty, "Value is null")
}

object Holder {

  val Key = "[]"
}