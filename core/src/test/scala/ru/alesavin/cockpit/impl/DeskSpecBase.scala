package ru.alesavin.cockpit.impl

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{Assertion, Matchers, WordSpec}
import ru.alesavin.cockpit.impl.TestControlTypes.ComplexType
import ru.alesavin.cockpit.model.{Control, ControlTypes, Desk}

import scala.concurrent.Future
import scala.concurrent.duration.{FiniteDuration, _}
import scala.reflect.ClassTag

/**
  * TODO
  *
  * @author alesavin
  */
trait DeskSpecBase
  extends WordSpec
    with Matchers
    with ScalaFutures {

  val DefaultFeatureTypes =
    new CompositeControlTypes(Iterable(
      BasicControlTypes,
      TestControlTypes))

  def syncPeriod: FiniteDuration = 1.millis

  def desk(ft: ControlTypes = DefaultFeatureTypes): Desk[Future]

  "Desk" should {

    import BasicControlTypes._

/* TODO
    "fail if construct registry with duplicate types" in {
      intercept[IllegalArgumentException] {
        desk(new CompositeControlTypes(Iterable(BasicControlTypes, BasicControlTypes)))
      }
    }
*/
    "register all features with CompositeFeatureTypes" in {
      val d = desk()
      d.register("bool_feature", false).value shouldBe false
      d.register("byte_feature", 0x1).value shouldBe 0x1
      d.register("short_feature", Short.MaxValue).value shouldBe Short.MaxValue
      d.register("int_feature", 1).value shouldBe 1
      d.register("float_feature", 6.0f).value shouldBe 6.0f
      d.register("double_feature", 5.0).value shouldBe 5.0
      d.register("str_feature", "value").value shouldBe "value"
      d.register("custom_feature", ComplexType(0,  0)).value shouldBe ComplexType(0, 0)
    }
    "return all registered features" in {
      val d = desk()
      d.register("str_feature", "value")
      d.register("bool_feature", false)
      d.register("int_feature", 1)
      val features = d.list.futureValue
        .map(f => f.name -> f.value.value).toMap
      features("str_feature") shouldBe "value"
      features("bool_feature") shouldBe "false"
      features("int_feature") shouldBe "1"
    }
    "update feature value with correct type" in {
      val d = desk()
      val name = "ct"
      val value = ComplexType(0, 0)

      val f = d.register(name, value)
      f match {
        case f: Control[_] =>
          f.name shouldBe name
          f.value shouldBe value
        case other => fail(s"Unexpected $other")
      }

      val newValue = ComplexType(2, 5)
      d.update(name, newValue.toString).futureValue
      Thread.sleep(syncPeriod.toMillis)

      f match {
        case f: Control[_] =>
          f.name shouldBe name
          f.value shouldBe newValue
        case other => fail(s"Unexpected $other")
      }

      d.update(name, "123")
        .shouldCompleteWithException[NoSuchElementException] // TODO IllegalArgumentException
    }
    "fail to update feature value with wrong type" in {
      val d = desk()
      val name = "int_feature"

      d.register(name, 122)
      d.update(name, "new_value")
        .shouldCompleteWithException[IllegalArgumentException]
    }
    "delete registered feature and return true" in {
      val d = desk()
      val name = "int_feature"

      d.register(name, 122)
      d.delete(name).futureValue shouldBe true
      d.list.futureValue shouldBe empty
    }
    "return false on calling delete for nonexistent feature" in {
      val d = desk()
      d.delete("new_feature").futureValue shouldBe false
    }
  }

  implicit class TestFuture[T](val future: Future[T]) {
    def shouldCompleteWithException[A <: Throwable: ClassTag]: Assertion =
      whenReady(future.failed) { e =>
        e shouldBe a [A]
      }
  }
}