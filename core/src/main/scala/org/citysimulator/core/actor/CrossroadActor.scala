package org.citysimulator.core.actor

import akka.actor.{Actor, ActorLogging, ActorRef, Props}

import org.citysimulator.core.actor.shutdown.SlaveShutdownHandlerActor
import org.citysimulator.core.business.citizen.{Citizen, CitizenAction, CitizenStatus}
import org.citysimulator.core.business.map.Crossroad
import org.citysimulator.core.business.vehicle._
import org.citysimulator.core.message.AODV.{FindRoute, RouteFound}
import org.citysimulator.core.message.Bootstrap._
import org.citysimulator.core.message.Crossroad._
import org.citysimulator.core.message.Life.MakeYourLife
import org.citysimulator.core.message.Observable._
import org.citysimulator.core.message.ShutdownHandler.WaitMeBeforeShuttingDown
import org.citysimulator.core.message.Shutdown.StartShutdown
import org.citysimulator.core.message.Travel.NewTravel
import org.citysimulator.core.observable.ObservableActor

import scala.collection.mutable.ArrayBuffer

/**
 * Manages one of the city crossroads
 *
 * @param crossroad [[Crossroad]] that it must administrate
 */
class CrossroadActor(crossroad: Crossroad) extends Actor with ActorLogging with ObservableActor {
  private val busStop: ArrayBuffer[Citizen] = ArrayBuffer.empty[Citizen]
  private val garage: ArrayBuffer[Vehicle] = ArrayBuffer.empty[Vehicle]
  private var park: ArrayBuffer[Vehicle] = ArrayBuffer.empty[Vehicle]

  private val life: ActorRef = context.actorOf(Props[LifeActor], "life")
  private val routing: ActorRef = context.actorOf(Props(new AODVActor(crossroad.name, crossroad.neighborsList)), "routing")
  private val shutdown: ActorRef = context.actorOf(Props[SlaveShutdownHandlerActor], "shutdown")
  private val travel: ActorRef = context.actorOf(Props[TravelActor], "travel")

  life ! RegisterListener(self)
  travel ! RegisterListener(self)

  shutdown ! WaitMeBeforeShuttingDown(life)
  shutdown ! WaitMeBeforeShuttingDown(routing)
  shutdown ! WaitMeBeforeShuttingDown(travel)

  context.parent ! BirthComplete(self)
  log.debug(s"send birth complete to parent")

  /**
   * Manages the incoming messages that the actor receives when it is in bootstrap phase
   */
  protected def bootstrapPhaseReceive: Receive = {
    case ConnectionComplete(actor: ActorRef) =>
      context.parent ! ConnectionComplete(self)
      log.debug(s"send connection complete message to parent")

    case Park(vehicle: Vehicle) =>
      park += vehicle
      log.debug(s"park ${vehicle.toString}")

    case StartConnection() =>
      routing ! StartConnection()
      log.debug(s"send connection message to routing")

    case StartSimulation() =>
      park.par.foreach(vehicle => {
        self ! Cross(vehicle)
        log.debug(s"${vehicle.toString} start simulation")
      })

      park.clear()
      log.debug(s"park is empty")

      context.become(observableReceive orElse operationPhaseReceive)
      log.debug(s"enter in operation mode")
  }

  /**
   * Manages the incoming messages that the actor receives when it is in operation phase
   */
  protected def operationPhaseReceive: Receive = {
    case Cross(vehicle: Vehicle) =>
      log.info(s"enter ${vehicle.toString}")
      if (vehicle.nextStop.equals(crossroad.name)) {
        vehicle match {
          case bus: Bus =>
            val descendant: Vector[Citizen] = bus.landing(crossroad.name)
            val ascendant: Vector[Citizen] = busStop.filter(citizen => {
              citizen.status match {
                case CitizenStatus.SLEEPING => bus.stopsList.contains(citizen.addressBook.get("work").get)
                case CitizenStatus.WORKING => bus.stopsList.contains(citizen.addressBook.get("home").get)
                case _: Any => false
              }
            }).toVector

            busStop --= ascendant

            descendant.par.foreach(citizen => {
              citizen.status = CitizenStatus.LANDING_FROM_BUS
              //TODO -> notify GUI event

              if (citizen.addressBook.get("home").get.equals(crossroad.name)) {
                life ! MakeYourLife(citizen, CitizenAction.GO_TO_SLEEPING)
              } else if (citizen.addressBook.get("work").get.equals(crossroad.name)) {
                life ! MakeYourLife(citizen, CitizenAction.GO_TO_WORK)
              }
            })

            ascendant.par.foreach(citizen => {
              citizen.status = CitizenStatus.BOARDING_ON_BUS
              //TODO -> notify GUI event

              val boardStatus: Boolean = bus.boarding(citizen)

              if (boardStatus) {
                citizen.status = CitizenStatus.TRAVELLING
                //TODO -> notify GUI event
              } else {
                citizen.status = CitizenStatus.GO_TO_BUS_STOP
                busStop += citizen
                //TODO -> notify GUI event
              }
            })

            bus.setNextStop()
            park += bus
            routing ! FindRoute(bus.nextStop)

          case car: Car =>
            val driver: Citizen = car.driver

            if (driver.addressBook.get("home").get.equals(crossroad.name)) {
              life ! MakeYourLife(driver, CitizenAction.GO_TO_SLEEPING)
            } else {
              life ! MakeYourLife(driver, CitizenAction.GO_TO_WORK)
            }

            garage += car

          case pawn: Pawn =>
            if (pawn.addressBook.get("home").get.equals(crossroad.name)) {
              life ! MakeYourLife(pawn, CitizenAction.GO_TO_SLEEPING)
            } else {
              life ! MakeYourLife(pawn, CitizenAction.GO_TO_WORK)
            }

            garage += pawn
        }
      } else {
        park += vehicle
        routing ! FindRoute(vehicle.nextStop)
      }

    case PrepareForTheTravel(citizen: Citizen) =>
      citizen.vehicle match {
        case Vehicles.BUS =>
          citizen.status = CitizenStatus.GO_TO_BUS_STOP
          busStop += citizen

        case Vehicles.CAR =>
          val vehicle: Option[Vehicle] = garage.find{
              case car: Car => car.driver.equals(citizen)
              case _: Any => false
            }

          if (vehicle.isDefined) {
            garage -= vehicle.get
            vehicle.get.setNextStop()
            park += vehicle.get
            routing ! FindRoute(vehicle.get.nextStop)
          }

        case Vehicles.PAWN =>
          val vehicle: Option[Vehicle] = garage.find{
            case pawn: Pawn => pawn.equals(citizen)
            case _: Any => false
          }

          if (vehicle.isDefined) {
            garage -= vehicle.get
            vehicle.get.setNextStop()
            park += vehicle.get
            routing ! FindRoute(vehicle.get.nextStop)
          }
      }

    case RouteFound(address: String, nextHop: ActorRef) =>
      val vehicles: Vector[Vehicle] = park.filter(vehicle => vehicle.nextStop.equals(address)).toVector

      park = park --= vehicles

      vehicles.par.foreach(vehicle => {
        travel ! NewTravel(vehicle, nextHop)
        log.debug(s"${vehicle.toString} start travel to reach ${nextHop.path}")
      })

    case StartShutdown() =>
      shutdown ! StartShutdown()
      log.debug(s"enter in shutdown mode")
  }

  /**
   * Manages the incoming messages
   */
  override def receive: Receive = bootstrapPhaseReceive
}