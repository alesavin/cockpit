package ru.alesavin.cockpit.model

/**
  * TODO
  *
  * @author alesavin
  */
trait ControlTypes {

  def forKey(key: ControlTypeKey): ControlType[_]
}

trait MapControlTypes extends ControlTypes {

  protected def types: Map[ControlTypeKey, ControlType[_]]

  override def forKey(key: ControlTypeKey): ControlType[_] =
    types(key)
}
