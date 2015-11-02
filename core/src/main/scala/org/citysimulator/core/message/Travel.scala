package org.citysimulator.core.message

import akka.actor.ActorRef

import org.citysimulator.core.business.vehicle.Vehicle

/**
 * Defines the messages that the travel manager actor must manage in order to simulate a travel that a [[Vehicle]] do
 * between two [[org.citysimulator.core.business.map.Crossroad]]
 */
object Travel {

  /**
   * The message inform the actor that a vehicle is starting a travel to reach a neighbor crossroad
   *
   * @param vehicle [[Vehicle]] who is start the travel
   * @param neighbor [[ActorRef]] of the neighbor crossroad that the vehicle reach at the end of the travel
   */
  case class NewTravel(vehicle: Vehicle, neighbor: ActorRef)

  /**
   * The message inform the actor that a vehicle made progress go through the street that link the two crossroad
   *
   * @param vehicle [[Vehicle]] who made the progress
   * @param neighbor [[ActorRef]] of the neighbor crossroad that the vehicle reach at the end of the travel
   */
  case class Progress(vehicle: Vehicle, neighbor: ActorRef)
}