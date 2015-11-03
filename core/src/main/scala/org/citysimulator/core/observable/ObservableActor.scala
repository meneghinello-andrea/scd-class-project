package org.citysimulator.core.observable

import akka.actor.{Actor, ActorLogging, ActorRef}

import org.citysimulator.core.message.Observable._

import scala.collection.mutable.ArrayBuffer

/**
 * Implements the operations that make an actor observable by another actor in the system
 */
trait ObservableActor { this: Actor with ActorLogging =>
  private val listeners: ArrayBuffer[ActorRef] = ArrayBuffer.empty[ActorRef]

  /**
   * Get a [[Vector]] with all the listeners registered
   *
   * @return Return a [[Vector]] of [[ActorRef]] actually registered
   */
  protected def listenersList: Vector[ActorRef] = listeners.toVector

  /**
   * Notify an event to all the interested actors
   *
   * @param event Event to be notified
   * @tparam T type of the event
   */
  protected def notifyEvent[T](event: T): Unit = {
    listeners.par.foreach(listener => listener ! event)
    log.debug(s"notified $event ")
  }

  /**
   * Manages the incoming messages which concern the implementation of the pattern
   */
  protected def observableReceive: Receive = {
    case RegisterListener(listener: ActorRef) =>
      if (!listeners.contains(listener)) {
        listeners += listener
        log.debug(s"${listener.path} registered as listener")
      } else {
        log.debug(s"${listener.path} already registered as listener")
      }

    case UnregisterListener(listener: ActorRef) =>
      listeners -= listener
      log.debug(s"${listener.path} unregistered as listener")
  }
}