package org.citysimulator.core.routing

import akka.actor.ActorRef

/**
 * Defines the operations that must be available in a routing table so that the routing algorithm can performs its
 * research with lower difficult
 */
trait Table {

  /**
   * Restore the table to the initial state
   */
  def clear(): Unit

  /**
   * Get the number of entry registered in the table
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
   * Refresh the entry in the table
   *
   * @param address [[String]] address name
   */
  def refresh(address: String): Unit

  /**
   * Remove an entry from the table
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