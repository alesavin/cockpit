package ru.alesavin.cockpit.model

/**
  * TODO
  *
  * @author alesavin
  */
trait Desk[F[_]] {

  def register[V: ControlType](name: String,
                               initialValue: V): Control[V]  // TODO F[Control[V]]?

  def list: F[Seq[Control[Holder]]]

  def update(name: String,
             value: String): F[Unit]

  def delete(name: String): F[Boolean]
}
