package ru.alesavin.cockpit.impl

import ru.alesavin.cockpit.model.{ControlTypes, Desk}

import scala.concurrent.Future

/**
  * TODO
  *
  * @author alesavin
  */
class InMemoryDeskSpec
  extends DeskSpecBase {

  def desk(ft: ControlTypes): Desk[Future] =
    new InMemoryDesk(ft)
}