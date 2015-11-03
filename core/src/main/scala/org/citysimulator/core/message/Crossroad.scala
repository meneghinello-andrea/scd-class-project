package org.citysimulator.core.message

import org.citysimulator.core.business.citizen.Citizen
import org.citysimulator.core.business.vehicle.Vehicle

/**
 * Defines the messages that the city crossroad actor must manage in order to administrate correctly the traffic inside
 * it
 */
object Crossroad {

  /**
   * The message inform the actor that a vehicle wants to cross the city crossroad
   *
   * @param vehicle [[Vehicle]] who is transit in the crossroad
   */
  case class Cross(vehicle: Vehicle)

  /**
   * The message inform the actor that a vehicle must be parked in the crossroad
   *
   * @param vehicle [[Vehicle]] that must be parked in the crossroad
   */
  case class Park(vehicle: Vehicle)

  /**
   * The message inform the actor that a citizen want to start a new trip
   *
   * @param citizen [[Citizen]] who want to start a new trip
   */
  case class PrepareForTheTravel(citizen: Citizen)
}