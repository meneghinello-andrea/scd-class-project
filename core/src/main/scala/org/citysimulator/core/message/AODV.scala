package org.citysimulator.core.message

import akka.actor.ActorRef

/**
 * Defines the messages that the actor that implement the AODV routing algorithm must manage in order to correctly
 * implement the algorithm
 */
object AODV {

  /**
   * The message inform the actor that it must start searching a route for a specific destination
   *
   * @param address [[String]] address name
   */
  case class FindRoute(address: String)

  /**
   * The message inform the actor that a route for a specific destination has been found by the algorithm and to reach
   * a destination the actor must talk with the next hop
   *
   * @param address [[String]] address name
   * @param nextHop [[ActorRef]] of the actor that has information about to reach the destination address
   */
  case class RouteFound(address: String, nextHop: ActorRef)

  /**
   * The message inform the actor that a route for a specific destination must be founded
   *
   * @param address [[String]] address name
   * @param timeToLive [[Int]] Time To Live (TTL) of the package
   */
  case class RouteRequest(address: String, timeToLive: Int)

  /**
   * The message inform the actor that a route for a specific destination has been found
   *
   * @param address [[String]] address name
   * @param nextHop [[ActorRef]] of the actor that has information about to reach the destination address
   * @param timeToLive [[Int]] Time To Live (TTL) of the package
   */
  case class RouteResponse(address: String, nextHop: ActorRef, timeToLive: Int)
}