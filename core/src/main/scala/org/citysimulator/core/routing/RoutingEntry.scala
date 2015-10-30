package org.citysimulator.core.routing

import akka.actor.ActorRef

/**
 * Represents a generic entry for the routing table and it contains information about the next hop to contact to reach
 * a destination address in the city
 *
 * @param address [[String]] address name
 * @param nextHop [[ActorRef]] of the next hop that has more information about the destination searched
 * @param inserted [[Long]] time in millis when the entry is inserted in the table
 */
case class RoutingEntry(address: String, var nextHop: Option[ActorRef], var inserted: Long)