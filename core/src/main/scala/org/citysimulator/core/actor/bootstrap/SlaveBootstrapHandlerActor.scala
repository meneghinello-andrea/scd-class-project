package org.citysimulator.core.actor.bootstrap

import akka.actor.ActorRef

import org.citysimulator.core.message.Bootstrap.{BirthComplete, BootstrapComplete}

/**
 * Defines the bootstrap strategies adopted by a slave actor in the system
 */
class SlaveBootstrapHandlerActor(private val chief: ActorRef, private val expected: Int)
  extends BootstrapHandler(expected) {

  /**
   * Communicate to master component the completion of the local birth phase
   */
  protected override def birthPhaseCompleteStrategy(): Unit = {
    chief ! BirthComplete(context.parent)
    log.debug(s"send birth complete to ${chief.path.name}")
  }

  /**
   * Communicate to master component the completion of the local bootstrap phase
   */
  protected override def connectionPhaseCompleteStrategy(): Unit = {
    chief ! BootstrapComplete(context.parent)
    log.debug(s"send bootstrap complete to ${chief.path.name}")
  }
}