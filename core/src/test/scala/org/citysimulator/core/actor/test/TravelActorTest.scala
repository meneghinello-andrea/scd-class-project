package org.citysimulator.core.actor.test

import akka.actor.{ActorRef, ActorSystem}
import akka.testkit.{ImplicitSender, TestActorRef, TestKit}

import org.citysimulator.core.actor.TravelActor
import org.citysimulator.core.business.vehicle.Bus
import org.citysimulator.core.message.Crossroad.Cross
import org.citysimulator.core.message.Shutdown.StartShutdown
import org.citysimulator.core.message.Travel.NewTravel

import org.scalatest.{BeforeAndAfterAll, MustMatchers, WordSpecLike}

import scala.concurrent.duration.DurationInt

/**
 * Test the [[TravelActor]] behaviour in common scenarios
 */
class TravelActorTest extends TestKit(ActorSystem("TravelActorTest")) with WordSpecLike
                                                                      with MustMatchers
                                                                      with ImplicitSender
                                                                      with BeforeAndAfterAll {
  /**
   * Terminate the actor system after that all the test cases are been executed
   */
  override def afterAll(): Unit = system.terminate()

  "The travel actor" must {
    "correctly simulate the through of a street that link two crossroad in city" in {
      val travelActor: ActorRef = TestActorRef[TravelActor]
      val bus: Bus = new Bus("123", "name", 5, Vector.empty[String])

      travelActor ! NewTravel(bus, testActor)

      expectMsg[Cross](8500.millis, Cross(bus))
      bus.travelProgress must be (100)
    }

    "correctly managed the planned event when it shutting down" in {
      val travelActor: ActorRef = TestActorRef[TravelActor]
      val bus: Bus = new Bus("123", "name", 5, Vector.empty[String])

      watch(travelActor)

      travelActor ! NewTravel(bus, testActor)
      travelActor ! StartShutdown()

      expectTerminated(travelActor, 1000.milliseconds)
    }
  }
}