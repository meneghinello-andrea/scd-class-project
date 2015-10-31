package org.citysimulator.core.business.vehicle

import org.citysimulator.core.business.citizen.Citizen

import scala.collection.mutable.ArrayBuffer

/**
 * Represents a bus that is able to transport many people from and to bus stops in the city
 */
case class Bus(id: String, run: String, capacity: Int, stopsList: Vector[String]) extends Vehicle {
  private val passengers: ArrayBuffer[Citizen] = ArrayBuffer.empty[Citizen]
  private var stopsIterator: Iterator[String] = if (stopsList.nonEmpty) stopsList.iterator else Iterator.empty

  /**
   * Boards a citizen on the bus
   *
   * @param citizen [[Citizen]] that is do the boarding phase
   * @return Return true if the [[Citizen]] has correctly complete the boarding phase, false otherwise
   */
  def boarding(citizen: Citizen): Boolean = {
    if (citizen.vehicle == Vehicles.BUS && nonFull && !passengers.contains(citizen)) {
      passengers += citizen
      true
    } else {
      false
    }
  }

  /**
   * Get the number of [[Citizen]] actually on the bus
   *
   * @return Return the [[Int]] number of passengers
   */
  def count: Int = passengers.length

  /**
   * Check if the bus is empty
   *
   * @return Return true if the bus is empty, false otherwise
   */
  def isEmpty: Boolean = count == 0

  /**
   * Lands the [[Citizen]] that are arrived to their stop
   *
   * @param stop [[String]] name of the actual bus stop
   * @return Return a [[Vector]] with the [[Citizen]] that has complete the landing phase
   */
  def landing(stop: String): Vector[Citizen] = {
    if (stopsList.contains(stop)) {
      val descendant: ArrayBuffer[Citizen] = passengers.filter(citizen => {citizen.addressBook.contains(stop)})
      passengers --= descendant
      descendant.toVector
    } else {
      Vector.empty[Citizen]
    }
  }

  /**
   * Check if the bus has available places for new passengers
   *
   * @return Return true if there are available places, false otherwise
   */
  def nonFull: Boolean = count < capacity

  /**
   * Get the list the bus passengers
   *
   * @return Return a [[Vector]] of [[Citizen]] actually on the bus
   */
  def passengersList: Vector[Citizen] = passengers.toVector

  /**
   * Set the address of the next stop that the vehicle must reach
   */
  override def setNextStop(): Unit = {
    if (stopsList.nonEmpty) {
      if (!stopsIterator.hasNext) {
        stopsIterator = stopsList.iterator
      }
      programmedStop = stopsIterator.next()
    } else {
      programmedStop = ""
    }
  }

  /**
   * Get the address in the city where the vehicle start the simulation
   *
   * @return Return the [[String]] address name
   */
  override def startAddress: String = stopsList.head
}