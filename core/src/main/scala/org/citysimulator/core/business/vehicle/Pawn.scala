package org.citysimulator.core.business.vehicle

import org.citysimulator.core.business.citizen.{AddressBook, Citizen, CitizenStatus}

/**
 * Represent a [[Citizen]] that travel in the city by foot
 *
 * @param id [[String]] identifier of the [[Citizen]]
 * @param name [[String]] name of the [[Citizen]]
 * @param addressBook [[AddressBook]] where the [[Citizen]] save important address
 */
class Pawn(id: String, name: String, addressBook: AddressBook = new AddressBook())
  extends Citizen(id, name, Vehicles.WALKER, addressBook) with Vehicle {

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