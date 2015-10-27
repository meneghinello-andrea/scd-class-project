package org.citysimulator.core.business.citizen

/**
 * Enumerates all the possible status on which a [[Citizen]] can be during the simulation
 */
object CitizenStatus extends Enumeration {
  type CitizenStatus = Value

  val BOARDING_ON_BUS, GO_TO_BUS_STOP, LANDING_FROM_BUS, SLEEPING, TRAVELLING, WAITING_BUS, WORKING = Value
}