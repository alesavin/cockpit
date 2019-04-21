package ru.alesavin.cockpit.impl

import org.apache.curator.framework.CuratorFramework
import org.apache.curator.utils.ZKPaths
import org.apache.zookeeper.{CreateMode, KeeperException}
import ru.alesavin.cockpit.impl.ZookeeperStorage._

import scala.util.Try
import scala.collection.JavaConverters._


/**
  * TODO
  *
  * @author alesavin
  */
class ZookeeperStorage(client: CuratorFramework,
                       baseZkPath: String) extends Storage {

  require(baseZkPath.nonEmpty, "Empty zookeeper path")

  override def keys: Try[Iterable[String]] =
    Try(client.getChildren.forPath(baseZkPath).asScala)

  override def get(key: String): Try[Option[String]] =
    Try {
      Option(client.getData.forPath(ZKPaths.makePath(baseZkPath, key))).map(to)
    }.recover {
      case _: KeeperException.NoNodeException => None
    }

  override def set(key: String, value: String): Try[Unit] =
    Try {
      client
        .create()
        .creatingParentsIfNeeded()
        .withMode(CreateMode.PERSISTENT)
        .forPath(ZKPaths.makePath(baseZkPath, key), from(value))
      ()
    }.recover {
      case _: KeeperException.NodeExistsException =>
        client
          .setData()
          .forPath(ZKPaths.makePath(baseZkPath, key), from(value))
        ()
    }

  override def remove(key: String): Try[Boolean] =
    Try {
      client.delete().forPath(ZKPaths.makePath(baseZkPath, key))
      true
    }.recover {
      case _: KeeperException.NoNodeException => false
    }
}

object ZookeeperStorage {

  def to(data: Array[Byte]): String = new String(data, "UTF-8")
  def from(data: String): Array[Byte] = data.getBytes("UTF-8")
}