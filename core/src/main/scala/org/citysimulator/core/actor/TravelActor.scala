package org.citysimulator.core.actor

import akka.actor.{Actor, ActorLogging, ActorRef, Cancellable, PoisonPill}

import org.citysimulator.core.business.map.Crossroad
import org.citysimulator.core.business.vehicle.{Bus, Car, Pawn, Vehicle}
import org.citysimulator.core.message.Crossroad.Cross
import org.citysimulator.core.message.Shutdown.StartShutdown
import org.citysimulator.core.message.Travel._

import scala.concurrent.duration.{DurationInt, FiniteDuration}

/**
 * The actor simulate the travel that a [[Vehicle]] do to reach a neighbor [[Crossroad]]
 */
class TravelActor extends Actor with ActorLogging {
  import context.dispatcher

  private val schedule: scala.collection.mutable.Map[Vehicle, Cancellable] =
    scala.collection.mutable.Map.empty[Vehicle, Cancellable]

  private val BUS_TIME: FiniteDuration = 800.millis
  private val CAR_TIME: FiniteDuration = 500.millis
  private val PAWN_TIME: FiniteDuration = 1200.millis

  /**
   * Manages the incoming messages that the actor receive when it is in operation phase
   */
  protected def operationPhase: Receive = {
    case NewTravel(vehicle: Vehicle, neighbor: ActorRef) =>
      vehicle.travelProgress = 0
      log.debug(s"[Actor (${self.path.name})]: $vehicle start trip to reach ${neighbor.path.name}")

      vehicle match {
        case bus: Bus =>
          val event: Cancellable = context.system.scheduler.scheduleOnce(BUS_TIME, self, Progress(bus, neighbor))
          schedule += (bus -> event)
          log.debug(s"[Actor (${self.path.name})]: planned event for ${bus.toString}")

        case car: Car =>
          val event: Cancellable = context.system.scheduler.scheduleOnce(CAR_TIME, self, Progress(car, neighbor))
          schedule += (car -> event)
          log.debug(s"[Actor (${self.path.name})]: planned event for ${car.toString}")

        case pawn: Pawn =>
          val event: Cancellable = context.system.scheduler.scheduleOnce(PAWN_TIME, self, Progress(pawn, neighbor))
          schedule += (pawn -> event)
          log.debug(s"[Actor (${self.path.name})]: planned event for ${pawn.toString}")
      }

    case Progress(vehicle: Vehicle, neighbor: ActorRef) =>
      vehicle.travelProgress += 10
      log.debug(s"[Actor (${self.path.name})]: ${vehicle.toString} made a progress (${vehicle.travelProgress})")

      val event: Option[Cancellable] = schedule.get(vehicle)

      if (event.isDefined) {
        event.get.cancel()
        log.debug(s"[Actor (${self.path.name})]: cancel the old event relative to ${vehicle.toString}")
      }

      schedule -= vehicle
      log.debug(s"[Actor (${self.path.name})]: cancel the old entry relative to ${vehicle.toString}")

      //TODO -> notify GUI event

      if (vehicle.travelProgress >= 100) {
        neighbor ! Cross(vehicle)
        log.debug(s"[Actor (${self.path.name})]: pass the vehicle to the neighbor crossroad ${neighbor.path.name}")
      } else {
        vehicle match {
          case bus: Bus =>
            val event: Cancellable = context.system.scheduler.scheduleOnce(BUS_TIME, self, Progress(bus, neighbor))
            schedule += (bus -> event)
            log.debug(s"[Actor (${self.path.name})]: planned event for ${bus.toString}")

          case car: Car =>
            val event: Cancellable = context.system.scheduler.scheduleOnce(CAR_TIME, self, Progress(car, neighbor))
            schedule += (car -> event)
            log.debug(s"[Actor (${self.path.name})]: planned event for ${car.toString}")

          case pawn: Pawn =>
            val event: Cancellable = context.system.scheduler.scheduleOnce(PAWN_TIME, self, Progress(pawn, neighbor))
            schedule += (pawn -> event)
            log.debug(s"[Actor (${self.path.name})]: planned event for ${pawn.toString}")
        }
      }

    case StartShutdown() =>
      schedule.keys.par.foreach(vehicle => {
        val event: Option[Cancellable] = schedule.get(vehicle)

        if (event.isDefined) {
          event.get.cancel()
          log.debug(s"[Actor (${self.path.name})]: cancel the event relative to ${vehicle.toString}")
        }
      })

      schedule.clear()
      log.debug(s"[Actor (${self.path.name})]: clean the schedule")

      self ! PoisonPill
      log.debug(s"[Actor (${self.path.name})]: all planned event deleted so the actor terminate")
  }

  /**
   * Manages the incoming messages that the actor receive
   */
  override def receive: Receive = operationPhase
}