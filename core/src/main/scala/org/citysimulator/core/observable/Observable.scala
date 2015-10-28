package org.citysimulator.core.observable

import akka.actor.Actor.Receive

/**
 * Specify the operations that an observable actor must implement in order to implement correctly the pattern
 */
trait Observable {

  /**
   * Notify an event to all the interested actors
   *
   * @param event Event to be notified
   * @tparam T type of the event
   */
  protected def notifyEvent[T](event: T): Unit

  /**
   * Manages the incoming messages which concern the implementation of the pattern
   */
  protected def observableReceive: Receive
}