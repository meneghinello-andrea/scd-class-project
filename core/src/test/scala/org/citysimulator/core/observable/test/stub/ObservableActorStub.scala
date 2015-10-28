package org.citysimulator.core.observable.test.stub

import akka.actor.{Actor, ActorLogging}

import org.citysimulator.core.observable.ObservableActor

/**
 * Stub of an observable actor
 */
class ObservableActorStub extends Actor with ActorLogging with ObservableActor {

  /**
   * Manages the incoming messages
   */
  override def receive: Receive = observableReceive
}