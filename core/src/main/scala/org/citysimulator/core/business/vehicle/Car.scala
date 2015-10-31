package org.citysimulator.core.business.vehicle

import org.citysimulator.core.business.citizen.{Citizen, CitizenStatus}

/**
 * Represents a car that is able to transport a single [[Citizen]]
 *
 * @param id [[String]] identifier of the car
 * @param driver [[Citizen]] who drive the car
 */
case class Car(id: String, driver: Citizen) extends Vehicle {

  /**
   * Set the address of the next stop that the vehicle must reach
   */
  override def setNextStop(): Unit = {
    driver.status match {
      case CitizenStatus.SLEEPING => programmedStop = driver.addressBook.get("WORK").get
      case CitizenStatus.WORKING => programmedStop = driver.addressBook.get("HOME").get
      case _: Any =>
    }
  }

  /**
   * Get the address in the city where the vehicle start the simulation
   *
   * @return Return the [[String]] address name
   */
  override def startAddress: String = driver.addressBook.get("HOME").get
}