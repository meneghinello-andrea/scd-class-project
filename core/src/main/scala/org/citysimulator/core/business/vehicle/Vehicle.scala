package org.citysimulator.core.business.vehicle

/**
 * Defines the common operation to all the authorized vehicles
 */
trait Vehicle {
  protected var programmedStop: String = ""

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
}