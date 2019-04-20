package ru.alesavin.cockpit.model

import org.scalatest.{Matchers, WordSpec}
import ru.alesavin.cockpit.impl.TestControlTypes.{ComplexType, ComplexTypeControlType}

import scala.util.Try

/**
  * TODO
  *
  * @author alesavin
  */
class ControlTypeSpec
  extends WordSpec
  with Matchers {

  "Control" should {

    "should be equal to other with the same key" in {
      val complexType2 = new ControlType[ComplexType] {
        override def key: ControlTypeKey = ComplexTypeControlType.key
        override def to(elem: ComplexType): String = ???
        override def from(data: String): Try[ComplexType] = ???
      }
      complexType2 shouldBe ComplexTypeControlType
      complexType2.hashCode shouldBe ComplexTypeControlType.hashCode
    }
  }
}
