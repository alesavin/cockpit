package ru.alesavin.cockpit.impl

import org.apache.curator.framework.{CuratorFramework, CuratorFrameworkFactory}
import org.apache.curator.retry.ExponentialBackoffRetry
import org.apache.curator.test.TestingCluster
import ru.alesavin.cockpit.model.{ControlTypes, Desk}

import scala.concurrent.Future

/**
  * TODO
  *
  * @author alesavin
  */
class ZookeeperDeskSpec
  extends DeskSpecBase {

  def desk(ft: ControlTypes): Desk[Future] = {
    val zkCluster = new TestingCluster(3)
    zkCluster.start()

    val Curator: CuratorFramework = {
      val curatorFramework = CuratorFrameworkFactory.newClient(
        zkCluster.getConnectString,
        new ExponentialBackoffRetry(100, 3))
      curatorFramework.start()
      curatorFramework
    }
    val BasePath = "/test"
    Curator.create().creatingParentsIfNeeded().forPath(BasePath)

    val zkStorage =
      new ZookeeperStorage(Curator, BasePath)
    new ZookeeperDesk(zkStorage, ft)
  }
}
