package org.citysimulator.core.business.vehicle.test

import org.citysimulator.core.business.citizen.CitizenStatus
import org.citysimulator.core.business.vehicle.Pawn

import org.scalatest.{MustMatchers, WordSpec}

/**
 * Test the [[Pawn]] component's operation in common use scenarios
 */
class PawnTest extends WordSpec with MustMatchers {
  "A city pawn" must {
    "respect the planned travel" in {
      val pawn: Pawn = new Pawn("123", "name")
      pawn.addressBook.insert("HOME", "manhattan.upper_west_side.crossroad_01")
      pawn.addressBook.insert("WORK", "manhattan.lower_east_side.crossroad_04")

      pawn.nextStop must be("manhattan.lower_east_side.crossroad_04")

      pawn.status = CitizenStatus.WORKING
      pawn.setNextStop()

      pawn.nextStop must be("manhattan.upper_west_side.crossroad_01")

      pawn.status = CitizenStatus.SLEEPING
      pawn.setNextStop()

      pawn.nextStop must be("manhattan.lower_east_side.crossroad_04")
    }

    "non change the programmed destination if the citizen status does not coincide with one of its tasks" in {
      val pawn: Pawn = new Pawn("123", "name")
      pawn.addressBook.insert("HOME", "manhattan.upper_west_side.crossroad_01")
      pawn.addressBook.insert("WORK", "manhattan.lower_east_side.crossroad_04")

      pawn.nextStop must be("manhattan.lower_east_side.crossroad_04")

      pawn.status = CitizenStatus.BOARDING_ON_BUS
      pawn.setNextStop()

      pawn.nextStop must be("manhattan.lower_east_side.crossroad_04")
    }
  }
}