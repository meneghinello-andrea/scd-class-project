package org.citysimulator.core.actor

import akka.actor.{Actor, ActorLogging, ActorRef, Props}

import org.citysimulator.core.actor.bootstrap.MasterBootstrapHandlerActor
import org.citysimulator.core.actor.shutdown.MasterShutdownHandlerActor
import org.citysimulator.core.business.map.{City, District}
import org.citysimulator.core.business.vehicle._
import org.citysimulator.core.configuration.{Unmarshaller, UnmarshallerFactory}
import org.citysimulator.core.message.Bootstrap._
import org.citysimulator.core.message.BootstrapHandler.PhaseComplete
import org.citysimulator.core.message.Shutdown.StartShutdown
import org.citysimulator.core.message.ShutdownHandler.WaitMeBeforeShuttingDown

/**
 * Manages the city
 *
 * @param configuration [[String]] path to the configuration file
 */
class CityActor(configuration: String) extends Actor with ActorLogging {
  private val unmarshaller: Unmarshaller = UnmarshallerFactory.newXmlUnmarshaller(configuration)
  private val city: City = unmarshaller.citiesList.head
  private val vehicles: Vector[Vehicle] = unmarshaller.vehiclesList

  private val shutdown: ActorRef = context.actorOf(Props[MasterShutdownHandlerActor], "shutdown")
  private val bootstrap: ActorRef =
    context.actorOf(Props(new MasterBootstrapHandlerActor(city.districtsList.length)), "bootstrap")

  /**
   * Manages the incoming messages that the actor receive when it is in operation phase
   */
  protected def operationPhaseReceive: Receive = {
    case BirthComplete(actor: ActorRef) =>
      bootstrap ! PhaseComplete(actor)
      log.debug(s"bootstrap manager saw birth of ${actor.path.name}")

    case BootstrapComplete(actor: ActorRef) =>
      bootstrap ! PhaseComplete(actor)
      log.debug(s"bootstrap manager saw bootstrap of ${actor.path.name}")

    case StartPopulating(actors: Vector[ActorRef]) =>
      actors.foreach(district => {
        shutdown ! WaitMeBeforeShuttingDown(district)
        log.debug(s"shutdown manage ${district.path.name}")

        val vehiclesList: Vector[Vehicle] =
          vehicles.filter(vehicle => vehicle.startAddress.contains(district.path.name))

        district ! Populate(vehiclesList)
        log.debug(s"populate district ${district.path.name}")
      })

      actors.par.foreach(district => {
        district ! StartSimulation()
        log.debug(s"populate district ${district.path.name}")
      })

    case CanIBeUseful(actor: ActorRef) =>
      val district: Option[District] = city.districtsList.find(district => {
        district.host.address.equals(actor.path.address.host.get) &&
        district.host.port.equals(actor.path.address.port.get)
      })

      if (district.isDefined) {
        actor ! WelcomeInTheSystem(district.get)
        log.debug(s"host recognized and implement ${district.get.name}")
      } else {
        actor ! HostUnacknowledged()
        log.debug(s"host not recognized (${actor.path.name})")
      }

    case StartShutdown() =>
      shutdown ! StartShutdown()
      log.debug(s"enters in shutdown mode")
  }

  /**
   * Manages the incoming messages that the actor receive
   */
  override def receive: Receive = operationPhaseReceive
}
