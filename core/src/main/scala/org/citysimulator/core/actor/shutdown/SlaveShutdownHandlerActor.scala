package org.citysimulator.core.actor.shutdown

import akka.actor.PoisonPill

/**
 * Defines the bootstrap strategies adopted by a slave actor in the system
 */
class SlaveShutdownHandlerActor extends ShutdownHandler {

  /**
   * Sub classes can override this method to define an appropriate strategy adopted when all the sub components
   * have complete their termination
   */
  protected def shutdownCompleteStrategy(): Unit = {
    context.parent ! PoisonPill
    log.debug(s"terminate the slave actor")
  }
}