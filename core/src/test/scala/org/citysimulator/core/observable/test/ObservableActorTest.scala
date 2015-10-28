package org.citysimulator.core.observable.test

import akka.actor.{ActorRef, ActorSystem}
import akka.testkit.{ImplicitSender, TestActorRef, TestKit, TestProbe}

import org.citysimulator.core.message.Observable._
import org.citysimulator.core.observable.ObservableActor
import org.citysimulator.core.observable.test.stub.ObservableActorStub

import org.scalatest.{BeforeAndAfterAll, MustMatchers, PrivateMethodTester, WordSpecLike}

/**
 * Test the [[ObservableActor]] component's operation in common use scenarios
 */
class ObservableActorTest extends TestKit(ActorSystem("ObservableActorTest")) with WordSpecLike
                                                                              with MustMatchers
                                                                              with ImplicitSender
                                                                              with PrivateMethodTester
                                                                              with BeforeAndAfterAll {
  /**
   * Terminate the actor system after that all the test cases are been executed
   */
  override def afterAll(): Unit = system.terminate()

  "The observable actor" must {
    "have no listeners registered when the actor is born" in {
      val observableActor: ObservableActorStub = TestActorRef[ObservableActorStub].underlyingActor
      val listenersList = PrivateMethod[Vector[ActorRef]]('listenersList)

      observableActor.invokePrivate(listenersList()) must be (Vector.empty[ActorRef])
      observableActor.invokePrivate(listenersList()).length must be (0)
    }

    "allow the registration of a new listener" in {
      val observableActor: ObservableActorStub = TestActorRef[ObservableActorStub].underlyingActor
      val listenersList = PrivateMethod[Vector[ActorRef]]('listenersList)

      observableActor.invokePrivate(listenersList()) must be (Vector.empty[ActorRef])
      observableActor.invokePrivate(listenersList()).length must be (0)

      observableActor.receive(RegisterListener(testActor))

      observableActor.invokePrivate(listenersList()) must contain (testActor)
      observableActor.invokePrivate(listenersList()).length must be (1)
    }

    "not allow the registration of the same listener multiple times" in {
      val observableActor: ObservableActorStub = TestActorRef[ObservableActorStub].underlyingActor
      val listenersList = PrivateMethod[Vector[ActorRef]]('listenersList)

      observableActor.invokePrivate(listenersList()) must be (Vector.empty[ActorRef])
      observableActor.invokePrivate(listenersList()).length must be (0)

      observableActor.receive(RegisterListener(testActor))

      observableActor.invokePrivate(listenersList()) must contain (testActor)
      observableActor.invokePrivate(listenersList()).length must be (1)

      observableActor.receive(RegisterListener(testActor))

      observableActor.invokePrivate(listenersList()) must contain (testActor)
      observableActor.invokePrivate(listenersList()).length must be (1)
    }

    "allow the removal of a listener" in {
      val observableActor: ObservableActorStub = TestActorRef[ObservableActorStub].underlyingActor
      val listenersList = PrivateMethod[Vector[ActorRef]]('listenersList)

      observableActor.invokePrivate(listenersList()) must be (Vector.empty[ActorRef])
      observableActor.invokePrivate(listenersList()).length must be (0)

      observableActor.receive(RegisterListener(testActor))

      observableActor.invokePrivate(listenersList()) must contain (testActor)
      observableActor.invokePrivate(listenersList()).length must be (1)

      observableActor.receive(UnregisterListener(testActor))

      observableActor.invokePrivate(listenersList()) must be (Vector.empty[ActorRef])
      observableActor.invokePrivate(listenersList()).length must be (0)
    }

    "notify to all the listeners if an event occurs" in {
      val notifyEvent = PrivateMethod[Unit]('notifyEvent)
      val observableActor: ObservableActorStub = TestActorRef[ObservableActorStub].underlyingActor

      val listener_01: TestProbe = TestProbe()
      val listener_02: TestProbe = TestProbe()
      val listener_03: TestProbe = TestProbe()

      observableActor.receive(RegisterListener(listener_01.ref))
      observableActor.receive(RegisterListener(listener_02.ref))
      observableActor.receive(RegisterListener(listener_03.ref))

      observableActor.invokePrivate(notifyEvent("event"))

      listener_01.expectMsg("event")
      listener_02.expectMsg("event")
      listener_03.expectMsg("event")
    }
  }
}