package org.citysimulator.core.actor.bootstrap

import org.citysimulator.core.message.Bootstrap.{StartConnection, StartPopulating}

/**
 * Defines the bootstrap strategies adopted by a master actor in the system
 */
class MasterBootstrapHandlerActor(private val expected: Int) extends BootstrapHandler(expected) {

  /**
   * Sub classes can override this method to define an appropriate strategy adopted when all the sub components
   * have complete their birth
   */
  protected override def birthPhaseCompleteStrategy(): Unit = {
    componentsList.par.foreach(actor => {
      actor ! StartConnection()
      log.debug(s"[Actor (${self.path.name})]: sends start connection to ${actor.path.name}")
    })
  }

  /**
   * Sub classes can override this method to define an appropriate strategy adopted when all the sub components
   * have complete the connection
   */
  protected override def connectionPhaseCompleteStrategy(): Unit = {
    context.parent ! StartPopulating(componentsList)
    log.debug(s"[Actor (${self.path.name})]: sends start populating to ${context.parent.path.name}")
  }
}