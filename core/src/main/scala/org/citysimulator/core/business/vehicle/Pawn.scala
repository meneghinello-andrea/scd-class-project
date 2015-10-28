package org.citysimulator.core.business.vehicle

import org.citysimulator.core.business.citizen.{AddressBook, Citizen, CitizenStatus}

/**
 * Represent a [[Citizen]] that travel in the city by foot
 *
 * @param citizen [[Citizen]] who travel by foot
 */
class Pawn(private val citizen: Citizen)
  extends Citizen(citizen.id, citizen.name, citizen.vehicle, citizen.addressBook, citizen.status) with Vehicle {

  /**
   * Set the address of the next stop that the vehicle must reach
   */
  override def setNextStop(): Unit = {
    status match {
      case CitizenStatus.SLEEPING => programmedStop = addressBook.get("WORK").get
      case CitizenStatus.WORKING => programmedStop = addressBook.get("HOME").get
      case _: Any =>
    }
  }
}