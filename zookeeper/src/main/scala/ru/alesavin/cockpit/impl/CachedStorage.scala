package ru.alesavin.cockpit.impl

import com.google.common.cache.CacheBuilder

import scala.concurrent.duration.FiniteDuration
import scala.util.{Success, Try}

/**
  * Caching mix-in for [[Storage]]
  *
  * @author alesavin
  */
trait CachedStorage extends Storage {

  def duration: FiniteDuration

  private val Cache: com.google.common.cache.Cache[String, String] =
    CacheBuilder.newBuilder()
      .expireAfterWrite(duration.length, duration.unit)
      .build()

  abstract override def get(key: String): Try[Option[String]] =
    for {
      cached <- Try(Option(Cache.getIfPresent(key)))
      r <- cached match {
        case s@Some(_) => Success(s)
        case _ => super.get(key).map { v =>
          v.foreach(Cache.put(key, _))
          v
        }
      }
    } yield r


  abstract override def set(key: String, value: String): Try[Unit] =
    for {
      _ <- Try(Cache.invalidate(key))
      r <- super.set(key, value)
    } yield r
}
