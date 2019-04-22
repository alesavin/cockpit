package ru.alesavin.cockpit.model

/**
  * Cockpit control type class
  *
  * @author alesavin
  */
trait Control[V] {

  def name: String
  def value: V

  override def equals(obj: scala.Any): Boolean =
    obj match {
      case f: Control[V] => f.name == name
      case _ => false
    }

  override def hashCode: Int =
    name.hashCode

  override def toString: String = s"Control($name)"
}

object Control {

  def apply[V](_name: String,
               _value: String => V): Control[V] =
    new Control[V] {
      override def name: String = _name
      override def value: V = _value(name)
    }
}
