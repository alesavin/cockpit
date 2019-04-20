package ru.alesavin.cockpit.impl

import ru.alesavin.cockpit.impl.HoldersDesk.HolderControlType
import ru.alesavin.cockpit.model.{Holder, _}

import scala.util.Try

/**
  * TODO
  *
  * @author alesavin
  */
abstract class HoldersDesk[F[_]](ft: ControlTypes,
                                 hType: ControlType[Holder] = HolderControlType)
  extends Desk[F] {

  protected def decode[V](data: String): V =
    (for {
      dataHolder <- hType.from(data)
      ct = ft.forKey(dataHolder.`type`)
      v <- ct.from(dataHolder.value)
    } yield v.asInstanceOf[V]).get

  override def register[V: ControlType](name: String,
                                        initialValue: V): Control[V] = {
    val ft = implicitly[ControlType[V]]
    val holder = Holder(ft.key, ft.to(initialValue))
    val init = hType.to(holder)
    registerInner(name, init)
  }

  override def update(name: String,
                      value: String): F[Unit] =
    updateInner(name) { current =>
      (for {
        currentHolder <- hType.from(current)
        ct = ft.forKey(currentHolder.`type`)
        _ <- ct.from(value)
        next = hType.to(currentHolder.copy(value = value))
      } yield next).get
    }

  protected def registerInner[V](name: String,
                                 init: String): Control[V]

  protected def updateInner(name: String)
                           (current2next: String => String): F[Unit]
}

object HoldersDesk {

  implicit object HolderControlType extends ControlType[Holder] {
    override def key: ControlTypeKey = Holder.Key
    override def to(elem: Holder): String =
      s"""{"type":"${elem.`type`}","value":"${elem.value}"}"""
    override def from(data: String): Try[Holder] =
      for {
        parts <- Try(data.split("\""))
        t <- Try(parts(3))
        v <- Try(parts(7))
        h <- Try(Holder(t, v))
      } yield h
  }
}