package org.citysimulator.core.message

import org.citysimulator.core.business.citizen.Citizen
import org.citysimulator.core.business.citizen.CitizenAction.CitizenAction

/**
 * Defines the messages that the life manager must manage in order to simulate the life behaviour of the [[Citizen]]
 * that live in the city
 */
object Life {

  /**
   * The message inform the actor that a [[Citizen]] has reach one of the address where it can do its tasks
   *
   * @param citizen [[Citizen]] who finished the trip
   * @param action [[CitizenAction]] that the [[Citizen]] must perform
   */
  case class MakeYourLife(citizen: Citizen, action: CitizenAction)

  /**
   * The message inform the actor that a [[Citizen]] has complete its task and is ready to start a new travel
   *
   * @param citizen [[Citizen]] who finished its tasks
   */
  case class ResumeTrip(citizen: Citizen)
}