package org.citysimulator.core.actor

import akka.actor.{ActorLogging, Actor, Cancellable, PoisonPill}

import org.citysimulator.core.business.citizen.{Citizen, CitizenAction}
import org.citysimulator.core.business.citizen.CitizenAction.CitizenAction
import org.citysimulator.core.message.Crossroad.PrepareForTheTravel
import org.citysimulator.core.message.Life._
import org.citysimulator.core.message.Shutdown.StartShutdown

import scala.concurrent.duration.{DurationInt, FiniteDuration}

/**
 * The actor simulate the tasks that a [[Citizen]] do when it finish to travel
 */
class LifeActor extends Actor with ActorLogging {
  import context.dispatcher

  private val schedule: scala.collection.mutable.Map[Citizen, Cancellable] =
    scala.collection.mutable.Map.empty[Citizen, Cancellable]

  private val SLEEP_TIME: FiniteDuration = 5.seconds
  private val WORK_TIME: FiniteDuration = 5.seconds

  /**
   * Manages the incoming messages that the actor receive when it is in operation phase
   */
  protected def operationPhase: Receive = {
    case MakeYourLife(citizen: Citizen, action: CitizenAction) =>
      action match {
        case CitizenAction.GO_TO_SLEEPING =>
          val event: Cancellable = context.system.scheduler.scheduleOnce(SLEEP_TIME, self, ResumeTrip(citizen))
          schedule += (citizen -> event)
          log.debug(s"[Actor (${self.path.name})]: ${citizen.name} starts sleeping")

          //TODO -> Notify GUI event

        case CitizenAction.GO_TO_WORK =>
          val event: Cancellable = context.system.scheduler.scheduleOnce(WORK_TIME, self, ResumeTrip(citizen))
          schedule += (citizen -> event)
          log.debug(s"[Actor (${self.path.name})]: ${citizen.name} starts working")

          //TODO -> Notify GUI event

        case _: Any =>
      }

    case ResumeTrip(citizen: Citizen) =>
      val event: Option[Cancellable] = schedule.get(citizen)

      if (event.isDefined) {
        event.get.cancel()
        log.debug(s"[Actor (${self.path.name})]: cancel the old event relative to ${citizen.name}")
      }

      context.parent ! PrepareForTheTravel(citizen)
      log.debug(s"[Actor (${self.path.name})]: ${citizen.name} wants to start a travel")

    case StartShutdown() =>
      schedule.keys.par.foreach(citizen => {
        val event: Option[Cancellable] = schedule.get(citizen)

        if (event.isDefined) {
          event.get.cancel()
          log.debug(s"[Actor (${self.path.name})]: cancel the event relative to ${citizen.name}")
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