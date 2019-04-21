package ru.alesavin.cockpit.impl

import scala.util.Try

/**
  * TODO
  *
  * @author alesavin
  */
trait Storage {

  def keys: Try[Iterable[String]] // TODO Curator Async => Future
  def get(key: String): Try[Option[String]]
  def set(key: String, value: String): Try[Unit]
  def remove(key: String): Try[Boolean]
}
