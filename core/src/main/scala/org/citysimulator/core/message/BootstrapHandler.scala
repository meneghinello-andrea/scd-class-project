package org.citysimulator.core.message

import akka.actor.ActorRef

/**
 * Defines the messages that the bootstrap handler actor must manage in order to administrate correctly the system
 * bootstrap
 */
object BootstrapHandler {

  /**
   * The message inform the actor that another actor have complete one of the sub phases of the system bootstrap
   *
   * @param actor [[ActorRef]] of the actor that has complete the sub phase
   */
  case class PhaseComplete(actor: ActorRef)
}