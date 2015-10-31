package org.citysimulator.core.business.vehicle

/**
 * Defines the common operation to all the authorized vehicles
 */
trait Vehicle {
  protected var programmedStop: String = ""
  var travelProgress: Int = 0

  /**
   * Get the address that the vehicle must reach
   *
   * @return Return the [[String]] address of the next stop
   */
  def nextStop: String = {
    if (programmedStop == "") {
      setNextStop()
    }
    programmedStop
  }

  /**
   * Set the address of the next stop that the vehicle must reach
   */
  def setNextStop(): Unit

  /**
   * Get the address in the city where the vehicle start the simulation
   *
   * @return Return the [[String]] address name
   */
  def startAddress: String
}