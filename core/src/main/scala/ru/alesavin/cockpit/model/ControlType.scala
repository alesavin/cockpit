package ru.alesavin.cockpit.model

import scala.util.Try

/**
  * TODO
  *
  * @author alesavin
  */
trait ControlType[V] {

  def key: ControlTypeKey

  def to(elem: V): String

  def from(data: String): Try[V]

  override final def equals(obj: scala.Any): Boolean =
    obj match {
      case f: ControlType[V] => f.key == key
      case _ => false
    }

  override final def hashCode: Int =
    key.hashCode
}

