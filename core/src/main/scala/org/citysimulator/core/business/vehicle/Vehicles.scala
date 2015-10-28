package org.citysimulator.core.business.vehicle

import org.citysimulator.core.business.citizen.Citizen
import org.citysimulator.core.business.map.City

/**
 * Enumerates the vehicles that is authorized to travel and transport [[Citizen]] in the [[City]]
 */
object Vehicles extends Enumeration {
  type Vehicles = Value

  val BUS, CAR, PAWN = Value
}