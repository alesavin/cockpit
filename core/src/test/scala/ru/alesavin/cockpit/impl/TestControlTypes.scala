package ru.alesavin.cockpit.impl

import ru.alesavin.cockpit.model.{ControlType, ControlTypeKey, MapControlTypes}

import scala.util.Try
import scala.util.matching.Regex

/**
  * TODO
  *
  * @author alesavin
  */
object TestControlTypes extends MapControlTypes {

  case class ComplexType(a: Int, b: Int) {

    require(a >= 0, "a must be non negative")
    require(b >= 0, "b must be non negative")

    override def toString: String = s"$a + ${b}i"
  }
  object ComplexType {

    val Pattern: Regex = "([0-9]+) \\+ ([0-9]+)i".r

    def unapply(value: String): Option[ComplexType] =
      value match {
        case Pattern(a, b) =>
          Some(ComplexType(a.toInt, b.toInt))
        case _ => None
      }
  }

  implicit case object ComplexTypeControlType
    extends ControlType[ComplexType] {

    override def key: String = "ComplexType"

    override def to(elem: ComplexType): String = elem.toString

    override def from(data: String): Try[ComplexType] = Try(ComplexType.unapply(data).get)
  }

  protected lazy val types: Map[ControlTypeKey, ControlType[_]] =
    Seq(ComplexTypeControlType)
      .map { t => t.key -> t }
      .toMap
}
