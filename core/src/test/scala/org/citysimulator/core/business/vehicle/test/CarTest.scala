package org.citysimulator.core.business.vehicle.test

import org.citysimulator.core.business.citizen.{Citizen, CitizenStatus}
import org.citysimulator.core.business.vehicle.{Car, Vehicles}

import org.scalatest.{MustMatchers, WordSpec}

/**
 * Test the [[Car]] component's operation in common use scenarios
 */
class CarTest extends WordSpec with MustMatchers {
  "A city car" must {
    "respect the planned travel" in {
      val citizen: Citizen = new Citizen("123", "name", Vehicles.CAR)
      citizen.addressBook.insert("HOME", "manhattan.upper_west_side.crossroad_01")
      citizen.addressBook.insert("WORK", "manhattan.lower_east_side.crossroad_04")

      val car: Car = new Car("123", citizen)

      car.nextStop must be ("manhattan.lower_east_side.crossroad_04")

      citizen.status = CitizenStatus.WORKING
      car.setNextStop()

      car.nextStop must be ("manhattan.upper_west_side.crossroad_01")

      citizen.status = CitizenStatus.SLEEPING
      car.setNextStop()

      car.nextStop must be ("manhattan.lower_east_side.crossroad_04")
    }

    "non change the programmed destination if the citizen status does not coincide with one of its tasks" in {
      val citizen: Citizen = new Citizen("123", "name", Vehicles.CAR)
      citizen.addressBook.insert("HOME", "manhattan.upper_west_side.crossroad_01")
      citizen.addressBook.insert("WORK", "manhattan.lower_east_side.crossroad_04")

      val car: Car = new Car("123", citizen)

      car.nextStop must be ("manhattan.lower_east_side.crossroad_04")

      citizen.status = CitizenStatus.BOARDING_ON_BUS
      car.setNextStop()

      car.nextStop must be ("manhattan.lower_east_side.crossroad_04")
    }
  }
}