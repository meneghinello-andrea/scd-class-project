package org.citysimulator.core.business.vehicle.test

import org.citysimulator.core.business.citizen.Citizen
import org.citysimulator.core.business.vehicle.{Bus, Vehicles}
import org.citysimulator.core.constants.test.TestConstants._

import org.scalatest.{MustMatchers, WordSpec}

/**
 * Test the [[Bus]] component's operation in common use scenarios
 */
class BusTest extends WordSpec with MustMatchers {
  "A city bus" must {
    "respect the planned stops" in {
      val bus: Bus = new Bus("123", "name", 5, Vector[String](
        "manhattan.upper_west_side.crossroad_01",
        "manhattan.upper_east_side.crossroad_01",
        "manhattan.lower_east_side.crossroad_01",
        "manhattan.lower_west_side.crossroad_01"
      ))

      bus.nextStop must be ("manhattan.upper_west_side.crossroad_01")
      bus.setNextStop()
      bus.nextStop must be ("manhattan.upper_east_side.crossroad_01")
      bus.setNextStop()
      bus.nextStop must be ("manhattan.lower_east_side.crossroad_01")
      bus.setNextStop()
      bus.nextStop must be ("manhattan.lower_west_side.crossroad_01")
      bus.setNextStop()
      bus.nextStop must be ("manhattan.upper_west_side.crossroad_01")
    }

    "be empty when it starts the service in the city" in {
      val bus: Bus = new Bus("123", "name", 5, Vector[String](
        "manhattan.upper_west_side.crossroad_01",
        "manhattan.upper_east_side.crossroad_01",
        "manhattan.lower_east_side.crossroad_01",
        "manhattan.lower_west_side.crossroad_01"
      ))

      bus.count must be (0)
      bus.isEmpty must be (TRUE)
      bus.nonFull must be (TRUE)
      bus.passengersList must be (Vector.empty[Citizen])
    }

    "not accept citizen that must travel with a different vehicle" in {
      val bus: Bus = new Bus("123", "name", 5, Vector[String](
        "manhattan.upper_west_side.crossroad_01",
        "manhattan.upper_east_side.crossroad_01",
        "manhattan.lower_east_side.crossroad_01",
        "manhattan.lower_west_side.crossroad_01"
      ))
      val citizen: Citizen = new Citizen("123", "name", Vehicles.CAR)

      bus.boarding(citizen) must be (FALSE)

      bus.count must be (0)
      bus.isEmpty must be (TRUE)
      bus.nonFull must be (TRUE)
      bus.passengersList must be (Vector.empty[Citizen])
    }

    "be non empty when a citizen complete the boarding phase" in {
      val bus: Bus = new Bus("123", "name", 5, Vector[String](
        "manhattan.upper_west_side.crossroad_01",
        "manhattan.upper_east_side.crossroad_01",
        "manhattan.lower_east_side.crossroad_01",
        "manhattan.lower_west_side.crossroad_01"
      ))
      val citizen: Citizen = new Citizen("123", "name", Vehicles.BUS)

      bus.boarding(citizen) must be (TRUE)

      bus.count must be (1)
      bus.isEmpty must be (FALSE)
      bus.passengersList must contain (citizen)
    }

    "be empty when the last passenger complete the landing phase" in {
      val bus: Bus = new Bus("123", "name", 5, Vector[String](
        "manhattan.upper_west_side.crossroad_01",
        "manhattan.upper_east_side.crossroad_01",
        "manhattan.lower_east_side.crossroad_01",
        "manhattan.lower_west_side.crossroad_01"
      ))
      val citizen: Citizen = new Citizen("123", "name", Vehicles.BUS)
      citizen.addressBook.insert("home", "manhattan.upper_west_side.crossroad_01")

      bus.boarding(citizen) must be (TRUE)

      val descendant: Vector[Citizen] = bus.landing("manhattan.upper_west_side.crossroad_01")

      descendant must contain (citizen)
      descendant.length must be (1)

      bus.count must be (0)
      bus.isEmpty must be (TRUE)
      bus.nonFull must be (TRUE)
      bus.passengersList must not contain citizen
    }

    "not permit that a citizen complete the boarding phase if it is already a bus passenger" in {
      val bus: Bus = new Bus("123", "name", 5, Vector[String](
        "manhattan.upper_west_side.crossroad_01",
        "manhattan.upper_east_side.crossroad_01",
        "manhattan.lower_east_side.crossroad_01",
        "manhattan.lower_west_side.crossroad_01"
      ))
      val citizen: Citizen = new Citizen("123", "name", Vehicles.BUS)

      bus.boarding(citizen) must be (TRUE)

      bus.count must be (1)
      bus.nonFull must be (TRUE)
      bus.passengersList must contain (citizen)

      bus.boarding(citizen) must be (FALSE)

      bus.count must be (1)
      bus.nonFull must be (TRUE)
      bus.passengersList must contain (citizen)
    }

    "have unchanged passengers list when it doing a not planned stop" in {
      val bus: Bus = new Bus("123", "name", 5, Vector[String](
        "manhattan.upper_west_side.crossroad_01",
        "manhattan.upper_east_side.crossroad_01",
        "manhattan.lower_east_side.crossroad_01",
        "manhattan.lower_west_side.crossroad_01"
      ))
      val citizen: Citizen = new Citizen("123", "name", Vehicles.BUS)

      bus.boarding(citizen) must be (TRUE)

      bus.count must be (1)
      bus.nonFull must be (TRUE)
      bus.passengersList must contain (citizen)

      bus.landing("manhattan.upper_west_side.crossroad_02") must be (Vector.empty[Citizen])
    }

    "have unchanged passengers list when it doing a planned stop and non on boarding or landing" in {
      val bus: Bus = new Bus("123", "name", 5, Vector[String](
        "manhattan.upper_west_side.crossroad_01",
        "manhattan.upper_east_side.crossroad_01",
        "manhattan.lower_east_side.crossroad_01",
        "manhattan.lower_west_side.crossroad_01"
      ))
      val citizen: Citizen = new Citizen("123", "name", Vehicles.BUS)
      citizen.addressBook.insert("home", "manhattan.upper_east_side.crossroad_01")

      bus.boarding(citizen) must be (TRUE)

      bus.landing("manhattan.upper_west_side.crossroad_01") must be (Vector.empty[Citizen])
    }

    "let down only the passengers that have reached the correct bus stop" in {
      val bus: Bus = new Bus("123", "name", 5, Vector[String](
        "manhattan.upper_west_side.crossroad_01",
        "manhattan.upper_east_side.crossroad_01",
        "manhattan.lower_east_side.crossroad_01",
        "manhattan.lower_west_side.crossroad_01"
      ))
      val citizen_01: Citizen = new Citizen("123", "name", Vehicles.BUS)
      val citizen_02: Citizen = new Citizen("125", "name", Vehicles.BUS)

      citizen_01.addressBook.insert("home", "manhattan.upper_west_side.crossroad_01")
      citizen_02.addressBook.insert("home", "manhattan.upper_east_side.crossroad_01")

      bus.boarding(citizen_01) must be (TRUE)
      bus.boarding(citizen_02) must be (TRUE)

      bus.count must be (2)
      bus.passengersList must contain (citizen_01)
      bus.passengersList must contain (citizen_02)

      val descendant: Vector[Citizen] = bus.landing("manhattan.upper_west_side.crossroad_01")

      descendant must contain (citizen_01)
      descendant.length must be (1)

      bus.count must be (1)
      bus.passengersList must not contain citizen_01
      bus.passengersList must contain (citizen_02)
    }

    "not contain passengers over its declared capacity" in {
      val bus: Bus = new Bus("123", "name", 2, Vector[String](
        "manhattan.upper_west_side.crossroad_01",
        "manhattan.upper_east_side.crossroad_01",
        "manhattan.lower_east_side.crossroad_01",
        "manhattan.lower_west_side.crossroad_01"
      ))
      val citizen_01: Citizen = new Citizen("123", "name", Vehicles.BUS)
      val citizen_02: Citizen = new Citizen("125", "name", Vehicles.BUS)
      val citizen_03: Citizen = new Citizen("127", "name", Vehicles.BUS)

      bus.count must be (0)
      bus.nonFull must be (TRUE)

      bus.boarding(citizen_01) must be (TRUE)
      bus.count must be (1)
      bus.nonFull must be (TRUE)
      bus.passengersList must contain (citizen_01)

      bus.boarding(citizen_02) must be (TRUE)
      bus.count must be (2)
      bus.nonFull must be (FALSE)
      bus.passengersList must contain (citizen_01)
      bus.passengersList must contain (citizen_02)

      bus.boarding(citizen_03) must be (FALSE)
      bus.count must be (2)
      bus.nonFull must be (FALSE)
      bus.passengersList must contain (citizen_01)
      bus.passengersList must contain (citizen_02)
      bus.passengersList must not contain citizen_03
    }
  }
}