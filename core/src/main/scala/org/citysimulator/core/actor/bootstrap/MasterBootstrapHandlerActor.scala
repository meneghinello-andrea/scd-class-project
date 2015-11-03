package org.citysimulator.core.actor.bootstrap

import org.citysimulator.core.message.Bootstrap.{StartConnection, StartPopulating}

/**
 * Defines the bootstrap strategies adopted by a master actor in the system
 */
class MasterBootstrapHandlerActor(private val expected: Int) extends BootstrapHandler(expected) {

  /**
   * Communicate that the sub component must start the connection phase
   */
  protected override def birthPhaseCompleteStrategy(): Unit = {
    componentsList.par.foreach(actor => {
      actor ! StartConnection()
      log.debug(s"sends start connection to ${actor.path.name}")
    })
  }

  /**
   * Communicate that the sub component must start the populating phase
   */
  protected override def connectionPhaseCompleteStrategy(): Unit = {
    context.parent ! StartPopulating(componentsList)
    log.debug(s"sends start populating to ${context.parent.path.name}")
  }
}