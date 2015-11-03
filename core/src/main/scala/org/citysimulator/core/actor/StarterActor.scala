package org.citysimulator.core.actor

import akka.actor.{Actor, ActorLogging, ActorRef, PoisonPill, Props}
import akka.util.Timeout

import org.citysimulator.core.business.map.District
import org.citysimulator.core.message.Bootstrap.{CanIBeUseful, HostUnacknowledged, WelcomeInTheSystem}

import scala.concurrent.duration.DurationInt
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Manages the bootstrap phase of a new remote host that want to participate in the system
 *
 * @param city [[String]] city name
 * @param address [[String]] IP address of the city host
 * @param port [[Int]] TCP network port on which the city host listen for incoming connections
 */
class StarterActor(city: String, address: String, port: Int) extends Actor with ActorLogging {
  private val cityAddress: String = s"""akka.tcp://$city@$address:$port/user/city"""
  private var cityHost: Option[ActorRef] = None
  private implicit val timeout: Timeout = new Timeout(60.seconds)

  for (reference <- context.actorSelection(cityAddress).resolveOne()) {
    cityHost = Some(reference)

    reference ! CanIBeUseful(self)
    log.debug(s"sent request of participation")
  }

  /**
   * Manages the incoming messages that the actor receive when it is in operation phase
   */
  protected def operationPhaseReceive: Receive = {
    case HostUnacknowledged() =>
      self ! PoisonPill
      log.debug(s"host unacknowledged starting shutting down")

      context.system.terminate()
      log.debug(s"terminate the local system")

    case WelcomeInTheSystem(district: District) =>
      val name: String = district.name.split('.').last
      context.system.actorOf(Props(new DistrictActor(cityHost.get, district)), name)
      log.debug(s"create the district manager")

      self ! PoisonPill
      log.debug(s"starter complete its tasks")
  }

  /**
   * Manages the incoming messages that the actor receive
   */
  override def receive: Receive = operationPhaseReceive
}