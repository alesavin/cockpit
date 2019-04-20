package ru.alesavin.cockpit.impl

import ru.alesavin.cockpit.model.{ControlType, ControlTypeKey, ControlTypes}

import scala.util.{Failure, Success, Try}

/**
  * TODO
  *
  * @author alesavin
  */
class CompositeControlTypes(delegates: Iterable[ControlTypes] = Iterable(BasicControlTypes))
  extends ControlTypes {

  override def forKey(key: ControlTypeKey): ControlType[_] =
    delegates.foldLeft(Failure(new NoSuchElementException()) : Try[ControlType[_]] ) { case (r, d) =>
        r match {
          case ct@Success(_) => ct
          case Failure(_) => Try(d.forKey(key))
        }
    }.get
}
