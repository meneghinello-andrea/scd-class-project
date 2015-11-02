package org.citysimulator.core.actor.shutdown

import akka.actor.{Actor, ActorIdentity, ActorLogging, ActorRef, Identify, PoisonPill, Terminated}

import org.citysimulator.core.message.Shutdown.StartShutdown
import org.citysimulator.core.message.ShutdownHandler.WaitMeBeforeShuttingDown

import scala.collection.mutable.ArrayBuffer

/**
 * The actor administrate the phases that compose the system shutdown
 */
abstract class ShutdownHandler extends Actor with ActorLogging {
  private val components: ArrayBuffer[ActorRef] = ArrayBuffer.empty[ActorRef]

  /**
   * Get a list the system components that must be closed before this components can terminate its tasks
   *
   * @return Return a [[Vector]] of [[ActorRef]] that must be closed before this can terminate
   */
  protected def componentsList: Vector[ActorRef] = components.toVector

  /**
   * Manages the incoming messages that the actor receive when it is in operation phase
   */
  protected final def operationPhaseReceive: Receive = {
    case ActorIdentity(correlationId: Any, actor: Option[ActorRef]) =>
      if (actor.isDefined) {
        context.watch(actor.get)
        log.debug(s"[Actor (${self.path.name})]: watch ${actor.get.path.name}")

        components += actor.get
        log.debug(s"[Actor (${self.path.name})]: register ${actor.get.path.name}")
      }

    case StartShutdown() =>
      if (components.isEmpty) {
        self ! PoisonPill
        log.debug(s"[Actor (${self.path.name})]: all sub components terminated correctly so the manager terminate")

        shutdownCompleteStrategy()
        log.debug(s"[Actor (${self.path.name})]: apply the shutdown strategy")
      } else {
        components.par.foreach(actor => {
          actor ! StartShutdown()
          log.debug(s"[Actor (${self.path.name})]: send shutdown message to ${actor.path.name}")
        })

        context.become(shutdownPhaseReceive)
        log.debug(s"[Actor (${self.path.name})]: enter in shutdown mode")
      }

    case Terminated(actor: ActorRef) =>
      context.unwatch(actor)
      log.debug(s"[Actor (${self.path.name})]: unwatch ${actor.path.name}")

      components -= actor
      log.debug(s"[Actor (${self.path.name})]: unregister ${actor.path.name}")

      context.actorSelection(actor.path) ! Identify
      log.debug(s"[Actor (${self.path.name})]: starts identification of the new reference of ${actor.path.name}")

    case WaitMeBeforeShuttingDown(actor: ActorRef) =>
      if (!components.contains(actor)) {
        context.watch(actor)
        log.debug(s"[Actor (${self.path.name})]: watch ${actor.path.name}")

        components += actor
        log.debug(s"[Actor (${self.path.name})]: register ${actor.path.name}")
      } else {
        log.debug(s"[Actor (${self.path.name})]: already watch ${actor.path.name}")
      }
  }

  /**
   * Sub classes can override this method to define an appropriate strategy adopted when all the sub components
   * have complete their termination
   */
  protected def shutdownCompleteStrategy(): Unit

  /**
   * Manages the incoming messages that the actor receive when it is in operation phase
   */
  protected final def shutdownPhaseReceive: Receive = {
    case Terminated(actor: ActorRef) =>
      context.unwatch(actor)
      log.debug(s"[Actor (${self.path.name})]: unwatch ${actor.path.name}")

      components -= actor
      log.debug(s"[Actor (${self.path.name})]: unregister ${actor.path.name}")

      if (components.isEmpty) {
        self ! PoisonPill
        log.debug(s"[Actor (${self.path.name})]: start termination")

        shutdownCompleteStrategy()
        log.debug(s"[Actor (${self.path.name})]: apply the shutdown strategy")
      } else {
        val remaining: Int = components.length
        log.debug(s"[Actor (${self.path.name})]: $remaining missing before the strategy application")
      }
  }

  /**
   * Manages the incoming messages
   */
  override def receive: Receive = operationPhaseReceive
}