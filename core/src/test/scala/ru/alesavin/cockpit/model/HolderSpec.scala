package ru.alesavin.cockpit.model

import org.scalatest.{Matchers, WordSpec}

import scala.util.Try

/**
  * TODO
  *
  * @author alesavin
  */
class HolderSpec
  extends WordSpec
  with Matchers {

  "Holder" should {

    "wrap type and value" in {
      Holder("a", "b")
      Holder("_", "1")
      Holder("asjsajhksdakjhjsakjdsa", "!@#@#$@#$$#$#@")
      Holder("a", "")
    }

    val FailCases = Seq(
      () => Holder("", ""),
      () => Holder("", "a"),
      () => Holder("b", None.orNull),
      () => Holder(None.orNull, "")
    )

    FailCases.zipWithIndex foreach { case (f, i) =>
      s"fail with case $i" in {
        Try(f()).isFailure shouldBe true
      }
    }

    "provide key" in {
      Holder.Key shouldBe "[]"
    }
  }
}
