package ru.alesavin.cockpit.impl

import org.scalatest.{Matchers, WordSpec}
import ru.alesavin.cockpit.impl.BasicControlTypes._
import ru.alesavin.cockpit.model.{ControlType, ControlTypeKey}

import scala.util.Success

/**
  * TODO
  *
  * @author alesavin
  */
class BasicControlTypesSpec
  extends WordSpec
  with Matchers {

  "BasicControlTypes" should {

    final case class TestCase(key: ControlTypeKey, v: Any)

    val Cases = Seq(
      TestCase("boolean", true),
      TestCase("boolean", false),
      TestCase("byte", 0x1a),
      TestCase("short", Short.MinValue),
      TestCase("int", 0),
      TestCase("int", -1001),
      TestCase("long", 0L),
      TestCase("long", Long.MaxValue),
      TestCase("float", 0f),
      TestCase("float", 0.0),
      TestCase("double", 0.0d),
      TestCase("double", Double.MinValue),
      TestCase("String", ""),
      TestCase("String", "_asdasd")
    )

    Cases.zipWithIndex foreach { case (TestCase(k, v), i) =>
      s"should be correct with $k, $v, $i" in {
        val ct = forKey(k)
        ct.from(ct.asInstanceOf[ControlType[Any]].to(v)) shouldBe Success(v)
        ct.key shouldBe k
      }
    }

    "fail for incorrect type" in {
      intercept[IllegalArgumentException] {
        anyValControlType[Char].from("c").get
      }
    }

  }
}
