package ru.alesavin.cockpit.impl

import ru.alesavin.cockpit.model.{ControlType, ControlTypeKey, MapControlTypes}

import scala.reflect.ClassTag
import scala.util.{Success, Try}

/**
  * TODO
  *
  * @author alesavin
  */
object BasicControlTypes extends MapControlTypes {

  implicit def anyValControlType[V <: AnyVal : ClassTag]: ControlType[V] =
    new ControlType[V] {
      override def key: ControlTypeKey =
        implicitly[ClassTag[V]].runtimeClass.getSimpleName
      override def to(elem: V): String =
        elem.toString
      override def from(data: String): Try[V] =
        Try {
          implicitly[ClassTag[V]].runtimeClass match {
            case c if c == classOf[Boolean] => data.toBoolean.asInstanceOf[V]
            case c if c == classOf[Byte] => data.toByte.asInstanceOf[V]
            case c if c == classOf[Short] => data.toShort.asInstanceOf[V]
            case c if c == classOf[Int] => data.toInt.asInstanceOf[V]
            case c if c == classOf[Long] => data.toLong.asInstanceOf[V]
            case c if c == classOf[Float] => data.toFloat.asInstanceOf[V]
            case c if c == classOf[Double] => data.toDouble.asInstanceOf[V]
            case c => throw new IllegalArgumentException(s"Can't deserialize type $c")
          }
        }
    }

  implicit object StringControlType extends ControlType[String] {
    override def key: ControlTypeKey = "String"

    override def to(elem: String): String =
      elem

    override def from(data: String): Try[String] =
      Success(data)
  }

  protected lazy val types: Map[ControlTypeKey, ControlType[_]] =
    Seq(
      anyValControlType[Boolean],
      anyValControlType[Byte],
      anyValControlType[Short],
      anyValControlType[Int],
      anyValControlType[Long],
      anyValControlType[Float],
      anyValControlType[Double],
      StringControlType
    )
    .map { t => t.key -> t }
    .toMap
}
