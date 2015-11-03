package org.citysimulator.core.actor.bootstrap

import akka.actor.{Actor, ActorLogging, ActorRef, PoisonPill}

import org.citysimulator.core.message.BootstrapHandler.PhaseComplete

import scala.collection.mutable.ArrayBuffer

/**
 * The actor administrate the phases that compose the system bootstrap
 *
 * @param expected [[Int]] number of components that id administrate
 */
abstract class BootstrapHandler(private val expected: Int) extends Actor with ActorLogging {
  private val components: ArrayBuffer[ActorRef] = ArrayBuffer.empty[ActorRef]

  /**
   * Sub classes can override this method to define an appropriate strategy adopted when all the sub components
   * have complete their birth
   */
  protected def birthPhaseCompleteStrategy(): Unit

  /**
   * Manages the incoming messages that actor receive when it is birth sub phase
   */
  protected final def birthPhaseReceive: Receive = {
    case PhaseComplete(actor: ActorRef) =>
      if (!components.contains(actor)) {
        components += actor
        log.debug(s"record the birth completion of ${actor.path.name}")

        if (components.length == expected) {
          birthPhaseCompleteStrategy()
          log.debug(s"executed birth complete strategy")

          components.clear()
          context.become(connectionPhaseReceive)
          log.debug(s"enters in connection phase management")
        } else {
          val remaining: Int = expected - components.length
          log.debug(s"$remaining missing before the strategy application")
        }
      }
  }

  /**
   * Get a list of the sub components that have complete the sub phase
   *
   * @return Return a [[Vector]] with [[ActorRef]] of actor that have complete one sub phase
   */
  protected def componentsList: Vector[ActorRef] = components.toVector

  /**
   * Sub classes can override this method to define an appropriate strategy adopted when all the sub components
   * have complete the connection
   */
  protected def connectionPhaseCompleteStrategy(): Unit

  /**
   * Manages the incoming message that the actor receive when it is in connection sub phase
   */
  protected final def connectionPhaseReceive: Receive = {
    case PhaseComplete(actor: ActorRef) =>
      if (!components.contains(actor)) {
        components += actor
        log.debug(s"record the connection completion of ${actor.path.name}")

        if (components.length == expected) {
          connectionPhaseCompleteStrategy()
          log.debug(s"executed connection complete strategy")

          components.clear()
          self ! PoisonPill
          log.debug(s"terminate the bootstrap phase")
        } else {
          val remaining: Int = expected - components.length
          log.debug(s"$remaining missing before the strategy application")
        }
      }
  }

  /**
   * Manages the incoming messages
   */
  override def receive: Receive = birthPhaseReceive
}