package ru.alesavin.cockpit.impl

import org.scalatest.{Matchers, WordSpec}
import ru.alesavin.cockpit.impl.TestControlTypes.ComplexType
import ru.alesavin.cockpit.model.{ControlType, ControlTypeKey, ControlTypes}

import scala.util.Success

/**
  * TODO
  *
  * @author alesavin
  */
class CompositeControlTypesSpec
  extends WordSpec
  with Matchers {

  private val Types: ControlTypes =
    new CompositeControlTypes(Iterable(
      BasicControlTypes,
      TestControlTypes))

  "CompositeControlTypes" should {

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
      TestCase("String", "_asdasd"),
      TestCase("ComplexType", ComplexType(0, 0)),
      TestCase("ComplexType", ComplexType(10, 55))
    )

    Cases.zipWithIndex foreach { case (TestCase(k, v), i) =>
      s"should be correct with $k, $v, $i" in {
        val ct = Types.forKey(k)
        ct.from(ct.asInstanceOf[ControlType[Any]].to(v)) shouldBe Success(v)
        ct.key shouldBe k
      }
    }

    "fail for unknown type" in {
      intercept[NoSuchElementException] {
        Types.forKey("__")
      }
    }
  }
}
