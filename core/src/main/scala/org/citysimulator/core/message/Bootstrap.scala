package org.citysimulator.core.message

import akka.actor.ActorRef

import org.citysimulator.core.business.map.District

/**
 * Defines the messages that the actors in the system exchange between them during the system bootstrap
 */
object Bootstrap {

  /**
   * The message inform the actor that one of its sub actors have complete the birth sub phase of the system
   * bootstrap
   *
   * @param actor [[ActorRef]] of the sub actor that has complete the sub phase
   */
  case class BirthComplete(actor: ActorRef)

  /**
   * The message inform the actor that one of its sub actors have complete the bootstrap phase of the system bootstrap
   *
   * @param actor [[ActorRef]] of the sub actor that has complete the bootstrap phase
   */
  case class BootstrapComplete(actor: ActorRef)

  /**
   * The message inform the actor that a new remote system is born and want has expressed its request of participate
   * as a sub component in the system
   *
   * @param actor [[ActorRef]] of the actor that has sent the participation request
   */
  case class CanIBeUseful(actor: ActorRef)

  /**
   * The message inform the the new system that it is not recognized as a valid host for the system
   */
  case class HostUnacknowledged()

  /**
   * The message inform the actor that it can start the connection phase
   */
  case class StartConnection()

  /**
   * The message inform the actor that it can start the population phase
   */
  case class StartPopulating()

  /**
   * The message inform the actor that it is recognized as a valid host for the system
   *
   * @param district [[District]] that the host must implement to be a valid host in the system
   */
  case class WelcomeInTheSystem(district: District)
}