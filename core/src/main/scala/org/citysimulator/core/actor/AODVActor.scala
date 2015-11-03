package org.citysimulator.core.actor

import akka.actor.{Actor, ActorLogging, ActorRef, PoisonPill, Terminated}
import akka.util.Timeout

import org.citysimulator.core.constants.AODV._
import org.citysimulator.core.message.AODV._
import org.citysimulator.core.message.Bootstrap.{ConnectionComplete, StartConnection}
import org.citysimulator.core.message.Shutdown.StartShutdown
import org.citysimulator.core.network.Host
import org.citysimulator.core.routing.{RoutingTable, Table}

import scala.collection.mutable.ArrayBuffer
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt

/**
 * The actor implements the AODV routing algorithm used to determine routes
 */
class AODVActor(addressName: String, neighborsList: Vector[Host]) extends Actor with ActorLogging {
  private implicit val timeout: Timeout = Timeout(60.seconds)
  private val neighbors: ArrayBuffer[ActorRef] = ArrayBuffer.empty[ActorRef]
  private val routingTable: Table = new RoutingTable(TABLE_CAPACITY)

  /**
   * Manages the incoming messages that the actor receive when it is in connection phase
   */
  protected def connectionPhase: Receive = {
    case StartConnection() =>
      neighborsList.par.foreach(neighborAddress => {
        val addressComponents: Array[String] = neighborAddress.name.split('.')
        val actorSystemName: String = addressComponents(1)
        val actorSystemAddress: String = neighborAddress.address
        val actorSystemPort: Int = neighborAddress.port
        val crossroadName: String = addressComponents.last
        val actorPath: String =
          s"""akka.tcp://$actorSystemName@$actorSystemAddress:$actorSystemPort/user/$actorSystemName/$crossroadName"""
        log.debug(s"start search of $actorPath}")

        for (neighbor <- context.actorSelection(actorPath).resolveOne()) {
          neighbors += neighbor
          log.debug(s"memorized the remote reference to ${neighbor.path.name}")

          context.watch(neighbor)
          log.debug(s"watch life behavior of ${neighbor.path.name}")
        }
      })

      context.parent ! ConnectionComplete(self)
      log.debug(s"parent informed about the completion of the connection sub phase")

      context.become(faultTolerantPhase orElse operationPhase)
      log.debug(s"enters in operation mode")
  }

  /**
   * Manages the incoming messages that the actor receive when it is in fault tolerance phase
   */
  protected def faultTolerantPhase: Receive = {
    case Terminated(actor: ActorRef) =>
      neighbors -= actor
      log.debug(s"removed the old reference to ${actor.path.name}")

      context.unwatch(actor)
      log.debug(s"unwatch life behaviour of the old reference of ${actor.path.name}")

      for(neighbor <- context.actorSelection(actor.path).resolveOne()) {
        neighbors += neighbor
        log.debug(s"memorized the remote reference to ${neighbor.path.name}")

        context.watch(neighbor)
        log.debug(s"watch life behavior of ${neighbor.path.name}")
      }
  }

  /**
   * Manages the incoming messages that the actor receive when it is in operation phase
   */
  protected def operationPhase: Receive = {
    case FindRoute(address: String) =>
      val route: Option[ActorRef] = routingTable.lookup(address)

      if (route.isDefined) {
        context.parent ! RouteFound(address, route.get)
        log.debug(s"parent must contact ${route.get.path.name} to reach $address")
      } else {
        self ! RouteRequest(address, TIME_TO_LIVE)
        log.debug(s"start route searching of $address")
      }

    case RouteRequest(address: String, timeToLive: Int) =>
      if (timeToLive > 0) {
        if (address.equals(addressName)) {
          //I'm the searched address
          self ! RouteResponse(address, self, TIME_TO_LIVE)
          log.debug(s"I'm the destination RREP for $address sent")
        } else {
          //I'm not the searched address
          neighbors.filter(neighbor => neighbor != self).par.foreach(neighbor => {
            neighbor ! RouteRequest(address, timeToLive - 1)
            log.debug(s"sent RREQ for $address to ${neighbor.path.name}")
          })
        }
      }

    case RouteResponse(address: String, nextHop: ActorRef, timeToLive: Int) =>
      routingTable.update(address, nextHop)
      log.debug(s"updated local routing table ($address, ${nextHop.path.name})")

      context.parent ! RouteFound(address, nextHop)
      log.debug(s"parent informed about the route for $address")

      if (timeToLive > 0) {
        neighbors.filter(neighbor => neighbor != self).par.foreach(neighbor => {
          neighbor ! RouteResponse(address, self, timeToLive - 1)
        })
      }

    case StartShutdown() =>
      neighbors.par.foreach(neighbor => {
        context.unwatch(neighbor)
        log.debug(s"unwatch life behaviour of the old reference of ${neighbor.path.name}")
      })

      self ! PoisonPill
      log.debug(s"terminate the routing algorithm")
  }

  /**
   * Manages the incoming messages that the actor receive
   */
  override def receive: Receive = connectionPhase
}