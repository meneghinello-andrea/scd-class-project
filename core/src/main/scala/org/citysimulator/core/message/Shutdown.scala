package org.citysimulator.core.message

/**
 * Defines the messages that the actors in the system exchange between them during the system shutdown
 */
object Shutdown {

  /**
   * The message inform the actor that it must start the shutdown phase in order to correctly close the entire system
   */
  case class StartShutdown()
}