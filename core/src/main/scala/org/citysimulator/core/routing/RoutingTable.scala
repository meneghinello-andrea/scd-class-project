package org.citysimulator.core.routing

import akka.actor.ActorRef

import java.util.Calendar

import scala.collection.mutable.ArrayBuffer
import scala.util.Random

/**
 * Implements the basic operations of a routing table
 *
 * @param capacity [[Int]] table capacity
 */
case class RoutingTable(capacity: Int) extends Table {
  private val emptyKey: String = "invalid.invalid.invalid"
  private val table: scala.collection.mutable.Map[Int, RoutingEntry] =
    scala.collection.mutable.Map.empty[Int, RoutingEntry]
  private val getOlderEntry =
    (left: (Int, RoutingEntry), right: (Int, RoutingEntry)) =>
      if (left._2.inserted <= right._2.inserted) left else right

  //Set the routing table to its initial status
  clear()

  /**
   * Get all the address key memorized in the table
   *
   * @return Return an [[Iterable]] collection with the key associated to the addresses
   */
  override def addresses: Iterable[String] = {
    val addressesList: ArrayBuffer[String] = ArrayBuffer.empty[String]

    table.keys.foreach(key => {
      val entry: Option[RoutingEntry] = table.get(key)
      if (!entry.get.address.contains(emptyKey)) {
        addressesList += entry.get.address
      }
    })

    addressesList.toSeq
  }

  /**
   * Empties the routing table
   */
  override def clear(): Unit = {
    table.clear()

    for (i <- 1 to capacity) {
      val key: String = s"$emptyKey.${Random.nextInt(1000)}"
      val timestamp: Long = Calendar.getInstance.getTimeInMillis
      val entry: RoutingEntry = new RoutingEntry(key, None, timestamp - i)

      table += (key.hashCode -> entry)
    }
  }

  /**
   * Get the number of addresses registered in the table
   *
   * @return Return the [[Int]] number of the entry actually registered
   */
  override def count: Int = table.count(entry => !entry._2.address.contains(emptyKey))

  /**
   * Insert a new route in the address book
   *
   * @param address [[String]] address name
   * @param nextHop [[ActorRef]] of the next hop that has more information about the destination searched
   */
  override def insert(address: String, nextHop: ActorRef): Unit = {

    //Get the older entry
    val olderEntry: (Int, RoutingEntry) = table.reduceLeft(getOlderEntry)

    //Remove the older entry to make place for the new one
    table -= olderEntry._1

    //Insert the new entry
    table += (address.hashCode -> new RoutingEntry(address, Some(nextHop), Calendar.getInstance().getTimeInMillis))
  }

  /**
   * Check if the table is empty
   *
   * @return Return true if the table is empty, false otherwise
   */
  override def isEmpty: Boolean = count == 0

  /**
   * Search information about a destination
   *
   * @param address [[String]] address name
   * @return Return an [[Option]] with the [[ActorRef]] to contact for have more information about the destination
   */
  override def lookup(address: String): Option[ActorRef] = {
    val entry: Option[RoutingEntry] = table.get(address.hashCode)

    if (entry.isDefined && entry.get.address.equals(address)) {
      entry.get.nextHop
    } else {
      None
    }
  }

  /**
   * Refresh the address time in the table
   *
   * @param address [[String]] address name
   */
  override def refresh(address: String): Unit = {
    val entry: Option[RoutingEntry] = table.get(address.hashCode)

    if (entry.isDefined && entry.get.address.equals(address)) {
      table.update(address.hashCode, new RoutingEntry(address, entry.get.nextHop, Calendar.getInstance.getTimeInMillis))
    }
  }

  /**
   * Remove an address from the table
   *
   * @param address [[String]] address name
   */
  override def remove(address: String): Unit = table -= address.hashCode

  /**
   * Update the information about a destination address if exists an entry associated, otherwise insert a new entry
   *
   * @param address [[String]] address name
   * @param nextHop [[ActorRef]] of the actor to contact for have more information about the destination
   */
  override def update(address: String, nextHop: ActorRef): Unit = {
    val entry: Option[RoutingEntry] = table.get(address.hashCode)

    if (entry.isDefined && entry.get.address.equals(address)) {
      table.update(address.hashCode, new RoutingEntry(address, Some(nextHop), Calendar.getInstance.getTimeInMillis))
    } else {
      insert(address, nextHop)
    }
  }
}