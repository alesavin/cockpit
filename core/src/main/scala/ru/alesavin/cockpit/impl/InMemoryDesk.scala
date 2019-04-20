package ru.alesavin.cockpit.impl

import ru.alesavin.cockpit.impl.HoldersDesk.HolderControlType
import ru.alesavin.cockpit.model._

import scala.collection.mutable
import scala.concurrent.Future
import scala.util.Try

/**
  * TODO
  *
  * @author alesavin
  */
class InMemoryDesk(ft: ControlTypes,
                   hType: ControlType[Holder] = HolderControlType)
  extends HoldersDesk[Future](ft, hType) {

  private val featureMap = mutable.Map[String, String]()

  private def getHolder(key: String): Holder =
    hType.from(featureMap(key)).get

  override def list: Future[Seq[Control[Holder]]] =
    Future.fromTry(Try {
      val keys = featureMap.keys
      keys.map(k => Control(k, getHolder)).toSeq
    })

  override def delete(name: String): Future[Boolean] = {
    Future.fromTry(Try {
      featureMap.remove(name) match {
        case Some(_) => true
        case None => false
      }
    })
  }

  override protected def registerInner[V](name: String,
                                          defaultValue: String): Control[V] =
    synchronized {
      featureMap.put(name, defaultValue)
      Control[V](name, _name => decode[V](featureMap(_name)))
    }

  override protected def updateInner(name: String)
                                    (current2next: String => String): Future[Unit] =
    synchronized { Future.fromTry(Try {
      val old = featureMap(name)
      val n = current2next(old)
      featureMap.update(name, n)
    })}
}
