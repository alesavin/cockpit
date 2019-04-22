package ru.alesavin.cockpit.impl

import org.apache.curator.framework.{CuratorFramework, CuratorFrameworkFactory}
import org.apache.curator.retry.ExponentialBackoffRetry
import org.apache.zookeeper.KeeperException
import org.scalatest.{Matchers, WordSpec}

import scala.util.{Failure, Success}

/**
  * Specs on [[ZookeeperStorage]]
  *
  * @author alesavin
  */
class ZookeeperStorageSpec
  extends WordSpec
  with Matchers {

  import org.apache.curator.test.TestingCluster

  private val zkCluster = new TestingCluster(3)
  zkCluster.start()

  "ZookeeperStorage" should {

    val Curator: CuratorFramework = {
      val curatorFramework = CuratorFrameworkFactory.newClient(
        zkCluster.getConnectString,
        new ExponentialBackoffRetry(100, 3))
      curatorFramework.start()
      curatorFramework
    }
    val BasePath = "/test"
    val zkStorage =
      new ZookeeperStorage(Curator, BasePath)

    "fail if have no base path" in {
      zkStorage.keys match {
        case Failure(_ : KeeperException.NoNodeException) => info("Done")
        case other => fail(s"Unexpected $other")
      }
    }
    "list keys" in {
      Curator.create().creatingParentsIfNeeded().forPath(BasePath)
      zkStorage.keys match {
        case Success(ks) if ks.isEmpty => info("Done")
        case other => fail(s"Unexpected $other")
      }
    }
    "get non-exist key" in {
      zkStorage.get("k") match {
        case Success(None) => info("Done")
        case other => fail(s"Unexpected $other")
      }
    }
    "set data for new key" in {
      zkStorage.set("k", "v") match {
        case Success(()) => info("Done")
        case other => fail(s"Unexpected $other")
      }
      zkStorage.get("k") match {
        case Success(Some("v")) => info("Done")
        case other => fail(s"Unexpected $other")
      }
    }
    "update data for key" in {
      zkStorage.get("k") match {
        case Success(Some("v")) => info("Done")
        case other => fail(s"Unexpected $other")
      }
      zkStorage.set("k", "v2") match {
        case Success(()) => info("Done")
        case other => fail(s"Unexpected $other")
      }
      zkStorage.get("k") match {
        case Success(Some("v2")) => info("Done")
        case other => fail(s"Unexpected $other")
      }
    }
    "remove keys" in {
      zkStorage.remove("k2") match {
        case Success(false) => info("Done")
        case other => fail(s"Unexpected $other")
      }
      zkStorage.get("k") match {
        case Success(Some("v2")) => info("Done")
        case other => fail(s"Unexpected $other")
      }
      zkStorage.remove("k") match {
        case Success(true) => info("Done")
        case other => fail(s"Unexpected $other")
      }
      zkStorage.get("k") match {
        case Success(None) => info("Done")
        case other => fail(s"Unexpected $other")
      }
    }
  }
}

