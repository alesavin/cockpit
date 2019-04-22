package ru.alesavin.cockpit.impl

import java.util.NoSuchElementException

import org.apache.curator.framework.CuratorFramework
import ru.alesavin.cockpit.impl.HoldersDesk.HolderControlType
import ru.alesavin.cockpit.impl.ZookeeperDesk.registrationLock
import ru.alesavin.cockpit.model.{Control, ControlType, ControlTypes, Holder}

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.{Failure, Success, Try}

/**
  * Impl of [[ru.alesavin.cockpit.model.Desk]] over Zookeeper
  *
  * @author alesavin
  */
class ZookeeperDesk(client: CuratorFramework,
                    baseZkPath: String,
                    ft: ControlTypes,
                    hType: ControlType[Holder] = HolderControlType,
                    cacheDuration: FiniteDuration = 30.seconds)
  extends HoldersDesk[Future](ft, hType) {

  private val zkStorage =
    new ZookeeperStorage(client, baseZkPath)
      with CachedStorage {
      override def duration: FiniteDuration = cacheDuration
    }

  override def list: Future[Seq[Control[Holder]]] =
    Future.fromTry(
      for {
        keys <- zkStorage.keys
        result <- keys.foldLeft(Success(Seq.empty) : Try[Seq[Control[Holder]]]) {
          case (f@Failure(_), _) => f
          case (Success(s), k) =>
            val nc = for {
              optV <- zkStorage.get(k)
              v <- Try(optV.getOrElse(throw new NoSuchElementException(s"No $k")))
            } yield Control(k, _ => hType.from(v).get)
            nc.map(ch => s :+ ch)
        }
      } yield result
    )

  override def delete(name: String): Future[Boolean] =
    Future.fromTry(zkStorage.remove(name))

  override protected def registerInner[V](name: String,
                                          init: String): Control[V] =
    registrationLock.synchronized { // do registrations sequential due of zk performance
      (for {
        exist <- zkStorage.get(name)
        _ <- exist match {
          case None => zkStorage.set(name, init)
          case _ => Success(())
        }
      } yield Control[V](name, n => decode[V](zkStorage.get(n).get.get))) // TODO
        .get
    }

  protected def updateInner(name: String)
                           (updater: String => String): Future[Unit] =
    Future.fromTry {
      for {
        optExist <- zkStorage.get(name)
        exist <- Try(optExist.getOrElse(throw new NoSuchElementException(s"No $name")))
        _ <- zkStorage.set(name, updater(exist))
      } yield ()
    }
}

object ZookeeperDesk {

  private val registrationLock = new Object
}
