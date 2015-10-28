package org.citysimulator.core.configuration

import org.citysimulator.core.business.citizen.Citizen
import org.citysimulator.core.business.map.City
import org.citysimulator.core.business.vehicle.Vehicle

/**
 * Specify the operations that a concrete unmarshaller must implement in order to get the necessary data, from the
 * configuration file, to build the system
 */
trait Unmarshaller {

  /**
   * Get a [[Vector]] with the cities that the system must build during the bootstrap phase
   *
   * @return Return a [[Vector]] of [[City]] that the system must build
   */
  def citiesList: Vector[City]

  /**
   * Get a [[Vector]] with the citizen specified in the configuration file
   *
   * @return Return a [[Vector]] of [[Citizen]] that live in the city
   */
  def citizensList: Vector[Citizen]

  /**
   * Compute the number of critical components that constitute the system
   *
   * @return Return the [[Int]] number of components that the system build
   */
  protected def getComponents: Int

  /**
   * Get a [[Vector]] with the vehicles that will travelling in the system
   *
   * @return Return a [[Vector]] of [[Vehicle]] that will travel in the system
   */
  def vehiclesList: Vector[Vehicle]
}