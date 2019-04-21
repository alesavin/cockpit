package ru.alesavin.cockpit.impl

import java.util.NoSuchElementException

import ru.alesavin.cockpit.impl.HoldersDesk.HolderControlType
import ru.alesavin.cockpit.impl.ZookeeperDesk.registrationLock
import ru.alesavin.cockpit.model.{Control, ControlType, ControlTypes, Holder}

import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

/**
  * TODO
  *
  * @author alesavin
  */
class ZookeeperDesk(deskMap: Storage,
                    ft: ControlTypes,
                    hType: ControlType[Holder] = HolderControlType)
  extends HoldersDesk[Future](ft, hType) {

  override def list: Future[Seq[Control[Holder]]] =
    Future.fromTry(
      for {
        keys <- deskMap.keys
        result <- keys.foldLeft(Success(Seq.empty) : Try[Seq[Control[Holder]]]) {
          case (f@Failure(_), _) => f
          case (Success(s), k) =>
            val nc = for {
              optV <- deskMap.get(k)
              v <- Try(optV.getOrElse(throw new NoSuchElementException(s"No $k")))
            } yield Control(k, _ => hType.from(v).get)
            nc.map(ch => s :+ ch)
        }
      } yield result
    )

  override def delete(name: String): Future[Boolean] =
    Future.fromTry(deskMap.remove(name))

  override protected def registerInner[V](name: String,
                                          init: String): Control[V] =
    registrationLock.synchronized { // do registrations sequential due of zk performance
      (for {
        exist <- deskMap.get(name)
        _ <- exist match {
          case None => deskMap.set(name, init)
          case _ => Success(())
        }
      } yield Control[V](name, n => decode[V](deskMap.get(n).get.get))) // TODO
        .get
    }

  protected def updateInner(name: String)
                           (updater: String => String): Future[Unit] =
    Future.fromTry {
      for {
        optExist <- deskMap.get(name)
        exist <- Try(optExist.getOrElse(throw new NoSuchElementException(s"No $name")))
        _ <- deskMap.set(name, updater(exist))
      } yield ()
    }
}

object ZookeeperDesk {

  private val registrationLock = new Object
}
