package org.citysimulator.core.routing

import akka.actor.ActorRef

import java.util.Calendar

/**
 * Implements the basic operations of a routing table
 *
 * @param capacity [[Int]] table capacity
 */
case class RoutingTable(capacity: Int) extends Table {
  private val table: scala.collection.mutable.Map[String, RoutingEntry] =
    scala.collection.mutable.Map.empty[String, RoutingEntry]
  private val reduction =
    (left: (String, RoutingEntry), right: (String, RoutingEntry)) =>
      if (left._2.inserted < right._2.inserted) left else right

  clear()

  /**
   * Get an iterable collection with the address of the table's entries memorized
   *
   * @return Return an [[Iterable]] collection with the [[String]] address
   */
  protected def addresses: Iterable[String] = table.keys

  /**
   * Get the value associated to a key in the table
   *
   * @param address [[String]] address name
   * @return Return an [[Option]] value with the associated entry
   */
  protected def get(address: String): Option[RoutingEntry] = table.get(address)

  /**
   * Initialize the map with invalid values but allowing the proper functioning of the algorithms that manages
   * the data inside the table
   */
  override def clear(): Unit = {
    table.clear()

    for (i <- 1 to capacity) {
      val key: String = s"address.none.$i"
      val timestamp: Long = Calendar.getInstance.getTimeInMillis
      val entry: RoutingEntry = new RoutingEntry(key, None, timestamp - i)

      table += (key -> entry)
    }
  }

  /**
   * Get the number of entry registered in the table
   *
   * @return Return the [[Int]] number of the entry actually registered
   */
  override def count: Int = table.count(entry => !entry._1.contains("address.none"))

  /**
   * Insert a new route in the address book
   *
   * @param address [[String]] address name
   * @param nextHop [[ActorRef]] of the next hop that has more information about the destination searched
   */
  override def insert(address: String, nextHop: ActorRef): Unit = {

    //Get the key of the entry that is not used by most time
    val oldEntry: (String, RoutingEntry) = table.reduceLeft(reduction)

    //Remove the older entry to make room for the new entry
    table -= oldEntry._1

    //Insert the new entry in the table
    table += (address -> new RoutingEntry(address, Some(nextHop), Calendar.getInstance.getTimeInMillis))
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
    val entry: Option[RoutingEntry] = get(address)

    if (entry.isDefined) {
      entry.get.nextHop
    } else {
      None
    }
  }

  /**
   * Refresh the entry in the table
   *
   * @param address [[String]] address name
   */
  override def refresh(address: String): Unit = {
    val entry: Option[RoutingEntry] = table.get(address)

    if (entry.isDefined) {
      entry.get.inserted = Calendar.getInstance.getTimeInMillis
    }
  }

  /**
   * Remove an entry from the table
   *
   * @param address [[String]] address name
   */
  override def remove(address: String): Unit = table -= address

  /**
   * Update the information about a destination address if exists an entry associated, otherwise insert a new entry
   *
   * @param address [[String]] address name
   * @param nextHop [[ActorRef]] of the actor to contact for have more information about the destination
   */
  override def update(address: String, nextHop: ActorRef): Unit = {
    val entry: Option[RoutingEntry] = table.get(address)

    if (entry.isDefined) {
      entry.get.nextHop = Some(nextHop)
      entry.get.inserted = Calendar.getInstance.getTimeInMillis
    } else {
      insert(address, nextHop)
    }
  }
}