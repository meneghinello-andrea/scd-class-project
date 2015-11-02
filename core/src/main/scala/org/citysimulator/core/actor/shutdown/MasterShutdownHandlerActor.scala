package org.citysimulator.core.actor.shutdown

/**
 * Defines the shutdown strategies adopted by a master actor in the system
 */
class MasterShutdownHandlerActor extends ShutdownHandler {

  /**
   * Terminate the system
   */
  protected def shutdownCompleteStrategy(): Unit = {
    context.system.terminate()
    log.debug(s"[Actor (${self.path.name})]: terminate the system")
  }
}