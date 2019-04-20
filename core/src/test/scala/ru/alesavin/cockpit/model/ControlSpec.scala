package ru.alesavin.cockpit.model

import org.scalatest.{Matchers, WordSpec}

/**
  * TODO
  *
  * @author alesavin
  */
class ControlSpec
  extends WordSpec
  with Matchers {

  "ControlType" should {

    "should be equal to other with the same key" in {
      val c1 = Control("1", _.toInt)
      c1.name shouldBe "1"
      c1.value shouldBe 1
      val c2 = Control("1", _.toFloat)
      c2.name shouldBe "1"
      c1.value shouldBe 1.0f
      c1 shouldBe c2
      c1.hashCode shouldBe c2.hashCode
    }
  }
}
