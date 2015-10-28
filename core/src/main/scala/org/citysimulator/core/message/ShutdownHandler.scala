package org.citysimulator.core.message

import akka.actor.ActorRef

/**
 * Defines the messages that the shutdown handler actor must manage in order to administrate correctly the system
 * shutdown
 */
object ShutdownHandler {

  /**
   * The message inform the actor that it must wait that the specified component is finished before it can be closed
   *
   * @param actor [[ActorRef]] of dependant actor
   */
  case class WaitMeBeforeShuttingDown(actor: ActorRef)
}