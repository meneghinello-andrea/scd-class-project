package org.citysimulator.core.routing

import akka.actor.ActorRef

/**
 * Defines the operations that must be available in a routing table so that the routing algorithm can performs its
 * research with lower difficult
 */
trait Table {

  /**
   * Get all the address key memorized in the table
   *
   * @return Return an [[Iterable]] collection with the key associated to the addresses
   */
  def addresses: Iterable[String]

  /**
   * Empties the routing table
   */
  def clear(): Unit

  /**
   * Get the number of addresses registered in the table
   *
   * @return Return the [[Int]] number of the entry actually registered
   */
  def count: Int

  /**
   * Insert a new route in the address book
   *
   * @param address [[String]] address name
   * @param nextHop [[ActorRef]] of the next hop that has more information about the destination searched
   */
  def insert(address: String, nextHop: ActorRef): Unit

  /**
   * Check if the table is empty
   *
   * @return Return true if the table is empty, false otherwise
   */
  def isEmpty: Boolean

  /**
   * Search information about a destination
   *
   * @param address [[String]] address name
   * @return Return an [[Option]] with the [[ActorRef]] to contact for have more information about the destination
   */
  def lookup(address: String): Option[ActorRef]

  /**
   * Refresh the address time in the table
   *
   * @param address [[String]] address name
   */
  def refresh(address: String): Unit

  /**
   * Remove an address from the table
   *
   * @param address [[String]] address name
   */
  def remove(address: String): Unit

  /**
   * Update the information about a destination address if exists an entry associated, otherwise insert a new entry
   *
   * @param address [[String]] address name
   * @param nextHop [[ActorRef]] of the actor to contact for have more information about the destination
   */
  def update(address: String, nextHop: ActorRef): Unit
}