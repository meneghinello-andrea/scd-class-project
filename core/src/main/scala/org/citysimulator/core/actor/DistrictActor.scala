package org.citysimulator.core.actor

import akka.actor.{Actor, ActorLogging, ActorRef, Props}

import org.citysimulator.core.actor.bootstrap.SlaveBootstrapHandlerActor
import org.citysimulator.core.actor.shutdown.MasterShutdownHandlerActor
import org.citysimulator.core.business.map.District
import org.citysimulator.core.business.vehicle.Vehicle
import org.citysimulator.core.message.Bootstrap._
import org.citysimulator.core.message.BootstrapHandler.PhaseComplete
import org.citysimulator.core.message.Crossroad.Park
import org.citysimulator.core.message.Shutdown.StartShutdown

import scala.collection.mutable.ArrayBuffer

/**
 * Manages one district of the city
 *
 * @param city [[ActorRef]] of the city that manages this crossroad
 * @param district [[District]] to administrate
 */
class DistrictActor(city: ActorRef, district: District) extends Actor with ActorLogging {
  private val crossroads: ArrayBuffer[ActorRef] = ArrayBuffer.empty[ActorRef]
  private val shutdown: ActorRef = context.actorOf(Props[MasterShutdownHandlerActor], "shutdown")
  private val bootstrap: ActorRef =
    context.actorOf(Props(new SlaveBootstrapHandlerActor(city, district.crossroadsList.length)), "bootstrap")

  district.crossroadsList.par.foreach(crossroad => {
    val name: String = crossroad.name.split('.').last
    context.actorOf(Props(new CrossroadActor(crossroad)), name)
    log.debug(s"create crossroad $name")
  })

  /**
   * Manages the incoming messages that the actor receive when it is in operation phase
   */
  protected def operationPhaseReceive: Receive = {
    case BirthComplete(actor: ActorRef) =>
      if (!crossroads.contains(actor)) {
        crossroads += actor
        log.debug(s"register new crossroad ${actor.path.name}")
      }

      bootstrap ! PhaseComplete(actor)
      log.debug(s"bootstrap manager saw birth of ${actor.path.name}")

    case ConnectionComplete(actor: ActorRef) =>
      bootstrap ! PhaseComplete(actor)
      log.debug(s"bootstrap manager saw connection completion of ${actor.path.name}")

    case Populate(vehicles: Vector[Vehicle]) =>
      district.crossroadsList.par.foreach(crossroad => {
        val vehiclesList: Vector[Vehicle] = vehicles.filter(vehicle => vehicle.startAddress.contains(crossroad.name))
        val name: String = crossroad.name.split('.').last
        val actor: Option[ActorRef] = context.child(name)

        if (actor.isDefined) {
          vehiclesList.foreach(vehicle => {
            actor.get ! Park(vehicle)
            log.debug(s"send park message to ${actor.get.path}")
          })
        }
      })

    case StartConnection() =>
      crossroads.par.foreach(crossroad => {
        crossroad ! StartConnection()
        log.debug(s"send connection message to ${crossroad.path}")
      })

    case StartShutdown() =>
      shutdown ! StartShutdown()
      log.debug(s"enter in shutdown mode")

    case StartSimulation() =>
      district.crossroadsList.par.foreach(crossroad => {
        val name: String = crossroad.name.split('.').last
        val actor: Option[ActorRef] = context.child(name)

        if (actor.isDefined) {
          actor.get ! StartSimulation()
          log.debug(s"send connection message to ${actor.get.path.name}")
        }
      })
  }

  /**
   * Manages the incoming messages that the actor receive
   */
  override def receive: Receive = operationPhaseReceive
}
