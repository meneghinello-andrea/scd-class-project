package org.citysimulator.core.business.citizen

/**
 * Enumerates all the possible actions that a [[Citizen]] can perform in the city
 */
object CitizenAction extends Enumeration {
  type CitizenAction = Value

  val ENDS_TRAVELLING, GO_TO_SLEEPING, GO_TO_WORK, STARTS_TRAVELLING = Value
}