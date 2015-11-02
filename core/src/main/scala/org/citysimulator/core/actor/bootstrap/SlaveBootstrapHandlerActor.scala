package org.citysimulator.core.actor.bootstrap

import akka.actor.ActorRef

import org.citysimulator.core.message.Bootstrap.{BirthComplete, BootstrapComplete}

/**
 * Defines the bootstrap strategies adopted by a slave actor in the system
 */
class SlaveBootstrapHandlerActor(private val chief: ActorRef, private val expected: Int)
  extends BootstrapHandler(expected) {

  /**
   * Sub classes can override this method to define an appropriate strategy adopted when all the sub components
   * have complete their birth
   */
  protected override def birthPhaseCompleteStrategy(): Unit = {
    componentsList.par.foreach(actor => {
      chief ! BirthComplete(context.parent)
      log.debug(s"[Actor (${self.path.name})]: sends bootstrap complete to ${chief.path.name}")
    })
  }

  /**
   * Sub classes can override this method to define an appropriate strategy adopted when all the sub components
   * have complete the connection
   */
  protected override def connectionPhaseCompleteStrategy(): Unit = {
    chief ! BootstrapComplete(context.parent)
    log.debug(s"[Actor (${self.path.name})]: sends bootstrap complete to ${chief.path.name}")
  }
}