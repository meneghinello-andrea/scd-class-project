package org.citysimulator.core.configuration.test

import org.citysimulator.core.business.citizen.Citizen
import org.citysimulator.core.business.map.{City, Crossroad, District}
import org.citysimulator.core.business.vehicle._
import org.citysimulator.core.configuration.XMLUnmarshaller
import org.citysimulator.core.constants.test.TestConstants._
import org.citysimulator.core.exception.ConfigurationException
import org.citysimulator.core.network.Host

import org.scalatest.{MustMatchers, PrivateMethodTester, WordSpec}

/**
 * Test the [[XMLUnmarshaller]] component's operation in common use scenarios
 */
class XMLUnmarshallerTest extends WordSpec with MustMatchers with PrivateMethodTester {
  private val configurationFile: String = "core/src/test/resources/manhattan.xml"

  "The XML unmarshaller" must {
    "recognize a valid configuration file" in {
      new XMLUnmarshaller(configurationFile)
    }

    "recognize an invalid configuration file" in intercept[ConfigurationException] {
      new XMLUnmarshaller("core/src/test/resources/uncorrected.xml")
    }

    "not load an inexistent configuration file" in intercept[ConfigurationException] {
      new XMLUnmarshaller("core/scr/test/resources/notCorrect.xml")
    }

    "correctly compute the number of components" in {
      val getComponents = PrivateMethod[Int]('getComponents)
      val unmarshaller: XMLUnmarshaller = new XMLUnmarshaller(configurationFile)

      unmarshaller.invokePrivate(getComponents()) must be (9)
    }

    "correctly find the host addresses given the district name" in {
      val findHostAddress = PrivateMethod[String]('findHostAddress)
      val unmarshaller: XMLUnmarshaller = new XMLUnmarshaller(configurationFile)

      unmarshaller.invokePrivate(findHostAddress("manhattan.upper_west_side")) must be ("127.0.0.1")
      unmarshaller.invokePrivate(findHostAddress("manhattan.upper_east_side")) must be ("127.0.0.2")
      unmarshaller.invokePrivate(findHostAddress("manhattan.lower_west_side")) must be ("127.0.0.3")
      unmarshaller.invokePrivate(findHostAddress("manhattan.lower_east_side")) must be ("127.0.0.4")
    }

    "create city objects according to the configuration file" in {
      val unmarshaller: XMLUnmarshaller = new XMLUnmarshaller(configurationFile)
      val citiesList: Vector[City] = unmarshaller.citiesList
      val city: Option[City] = citiesList.headOption

      city.isDefined must be (TRUE)
      city.get.name must be ("manhattan")
      city.get.host.name must be ("manhattan")
      city.get.host.address must be ("127.0.0.0")
      city.get.host.port must be (2550)

      val upperWestSide: Option[District] =
        city.get.districtsList.find(district => district.name == "manhattan.upper_west_side")
      upperWestSide.isDefined must be (TRUE)
      upperWestSide.get.name must be ("manhattan.upper_west_side")
      upperWestSide.get.host.name must be ("manhattan.upper_west_side")
      upperWestSide.get.host.address must be ("127.0.0.1")
      upperWestSide.get.host.port must be (2551)

      val crossroad_01: Option[Crossroad] =
        upperWestSide.get.crossroadsList.find(crossroad => crossroad.name == "manhattan.upper_west_side.crossroad_01")
      crossroad_01.isDefined must be (TRUE)
      crossroad_01.get.name must be ("manhattan.upper_west_side.crossroad_01")
      crossroad_01.get.coordinate.latitude must be (25)
      crossroad_01.get.coordinate.longitude must be (25)
      crossroad_01.get.neighborsList.length must be (2)
      crossroad_01.get.neighborsList must contain (Host("manhattan.upper_west_side.crossroad_02", "127.0.0.1", 2551))
      crossroad_01.get.neighborsList must contain (Host("manhattan.upper_west_side.crossroad_03", "127.0.0.1", 2551))

      val crossroad_02: Option[Crossroad] =
        upperWestSide.get.crossroadsList.find(crossroad => crossroad.name == "manhattan.upper_west_side.crossroad_02")
      crossroad_02.isDefined must be (TRUE)
      crossroad_02.get.name must be ("manhattan.upper_west_side.crossroad_02")
      crossroad_02.get.coordinate.latitude must be (125)
      crossroad_02.get.coordinate.longitude must be (25)
      crossroad_02.get.neighborsList.length must be (3)
      crossroad_02.get.neighborsList must contain (Host("manhattan.upper_west_side.crossroad_01", "127.0.0.1", 2551))
      crossroad_02.get.neighborsList must contain (Host("manhattan.upper_east_side.crossroad_01", "127.0.0.2", 2552))
      crossroad_02.get.neighborsList must contain (Host("manhattan.upper_west_side.crossroad_04", "127.0.0.1", 2551))

      val crossroad_03: Option[Crossroad] =
        upperWestSide.get.crossroadsList.find(crossroad => crossroad.name == "manhattan.upper_west_side.crossroad_03")
      crossroad_03.isDefined must be (TRUE)
      crossroad_03.get.name must be ("manhattan.upper_west_side.crossroad_03")
      crossroad_03.get.coordinate.latitude must be (25)
      crossroad_03.get.coordinate.longitude must be (125)
      crossroad_03.get.neighborsList.length must be (3)
      crossroad_03.get.neighborsList must contain (Host("manhattan.upper_west_side.crossroad_01", "127.0.0.1", 2551))
      crossroad_03.get.neighborsList must contain (Host("manhattan.upper_west_side.crossroad_04", "127.0.0.1", 2551))
      crossroad_03.get.neighborsList must contain (Host("manhattan.lower_west_side.crossroad_01", "127.0.0.3", 2553))

      val crossroad_04: Option[Crossroad] =
        upperWestSide.get.crossroadsList.find(crossroad => crossroad.name == "manhattan.upper_west_side.crossroad_04")
      crossroad_04.isDefined must be (TRUE)
      crossroad_04.get.name must be ("manhattan.upper_west_side.crossroad_04")
      crossroad_04.get.coordinate.latitude must be (125)
      crossroad_04.get.coordinate.longitude must be (125)
      crossroad_04.get.neighborsList.length must be (4)
      crossroad_04.get.neighborsList must contain (Host("manhattan.upper_west_side.crossroad_02", "127.0.0.1", 2551))
      crossroad_04.get.neighborsList must contain (Host("manhattan.upper_east_side.crossroad_03", "127.0.0.2", 2552))
      crossroad_04.get.neighborsList must contain (Host("manhattan.upper_west_side.crossroad_03", "127.0.0.1", 2551))

      val upperEastSide: Option[District] =
        city.get.districtsList.find(district => district.name == "manhattan.upper_east_side")
      upperEastSide.isDefined must be (TRUE)
      upperEastSide.get.name must be ("manhattan.upper_east_side")
      upperEastSide.get.host.name must be ("manhattan.upper_east_side")
      upperEastSide.get.host.address must be ("127.0.0.2")
      upperEastSide.get.host.port must be (2552)

      val crossroad_05: Option[Crossroad] =
        upperEastSide.get.crossroadsList.find(crossroad => crossroad.name == "manhattan.upper_east_side.crossroad_01")
      crossroad_05.isDefined must be (TRUE)
      crossroad_05.get.name must be ("manhattan.upper_east_side.crossroad_01")
      crossroad_05.get.coordinate.latitude must be (325)
      crossroad_05.get.coordinate.longitude must be (25)
      crossroad_05.get.neighborsList.length must be (3)
      crossroad_05.get.neighborsList must contain (Host("manhattan.upper_east_side.crossroad_02", "127.0.0.2", 2552))
      crossroad_05.get.neighborsList must contain (Host("manhattan.upper_east_side.crossroad_03", "127.0.0.2", 2552))
      crossroad_05.get.neighborsList must contain (Host("manhattan.upper_west_side.crossroad_02", "127.0.0.1", 2551))

      val crossroad_06: Option[Crossroad] =
        upperEastSide.get.crossroadsList.find(crossroad => crossroad.name == "manhattan.upper_east_side.crossroad_02")
      crossroad_06.isDefined must be (TRUE)
      crossroad_06.get.name must be ("manhattan.upper_east_side.crossroad_02")
      crossroad_06.get.coordinate.latitude must be (425)
      crossroad_06.get.coordinate.longitude must be (25)
      crossroad_06.get.neighborsList.length must be (2)
      crossroad_06.get.neighborsList must contain (Host("manhattan.upper_east_side.crossroad_01", "127.0.0.2", 2552))
      crossroad_06.get.neighborsList must contain (Host("manhattan.upper_east_side.crossroad_04", "127.0.0.2", 2552))

      val crossroad_07: Option[Crossroad] =
        upperEastSide.get.crossroadsList.find(crossroad => crossroad.name == "manhattan.upper_east_side.crossroad_03")
      crossroad_07.isDefined must be (TRUE)
      crossroad_07.get.name must be ("manhattan.upper_east_side.crossroad_03")
      crossroad_07.get.coordinate.latitude must be (325)
      crossroad_07.get.coordinate.longitude must be (125)
      crossroad_07.get.neighborsList.length must be (4)
      crossroad_07.get.neighborsList must contain (Host("manhattan.upper_east_side.crossroad_01", "127.0.0.2", 2552))
      crossroad_07.get.neighborsList must contain (Host("manhattan.upper_east_side.crossroad_04", "127.0.0.2", 2552))
      crossroad_07.get.neighborsList must contain (Host("manhattan.lower_east_side.crossroad_01", "127.0.0.4", 2554))
      crossroad_07.get.neighborsList must contain (Host("manhattan.upper_west_side.crossroad_04", "127.0.0.1", 2551))

      val crossroad_08: Option[Crossroad] =
        upperEastSide.get.crossroadsList.find(crossroad => crossroad.name == "manhattan.upper_east_side.crossroad_04")
      crossroad_08.isDefined must be (TRUE)
      crossroad_08.get.name must be ("manhattan.upper_east_side.crossroad_04")
      crossroad_08.get.coordinate.latitude must be (425)
      crossroad_08.get.coordinate.longitude must be (125)
      crossroad_08.get.neighborsList.length must be (3)
      crossroad_08.get.neighborsList must contain (Host("manhattan.upper_east_side.crossroad_02", "127.0.0.2", 2552))
      crossroad_08.get.neighborsList must contain (Host("manhattan.lower_east_side.crossroad_02", "127.0.0.4", 2554))
      crossroad_08.get.neighborsList must contain (Host("manhattan.upper_east_side.crossroad_03", "127.0.0.2", 2552))

      val lowerWestSide: Option[District] =
        city.get.districtsList.find(district => district.name == "manhattan.lower_west_side")
      lowerWestSide.isDefined must be (TRUE)
      lowerWestSide.get.name must be ("manhattan.lower_west_side")
      lowerWestSide.get.host.name must be ("manhattan.lower_west_side")
      lowerWestSide.get.host.address must be ("127.0.0.3")
      lowerWestSide.get.host.port must be (2553)

      val crossroad_09: Option[Crossroad] =
        lowerWestSide.get.crossroadsList.find(crossroad => crossroad.name == "manhattan.lower_west_side.crossroad_01")
      crossroad_09.isDefined must be (TRUE)
      crossroad_09.get.name must be ("manhattan.lower_west_side.crossroad_01")
      crossroad_09.get.coordinate.latitude must be (25)
      crossroad_09.get.coordinate.longitude must be (325)
      crossroad_09.get.neighborsList.length must be (3)
      crossroad_09.get.neighborsList must contain (Host("manhattan.upper_west_side.crossroad_03", "127.0.0.1", 2551))
      crossroad_09.get.neighborsList must contain (Host("manhattan.lower_west_side.crossroad_02", "127.0.0.3", 2553))
      crossroad_09.get.neighborsList must contain (Host("manhattan.lower_west_side.crossroad_03", "127.0.0.3", 2553))

      val crossroad_10: Option[Crossroad] =
        lowerWestSide.get.crossroadsList.find(crossroad => crossroad.name == "manhattan.lower_west_side.crossroad_02")
      crossroad_10.isDefined must be (TRUE)
      crossroad_10.get.name must be ("manhattan.lower_west_side.crossroad_02")
      crossroad_10.get.coordinate.latitude must be (125)
      crossroad_10.get.coordinate.longitude must be (325)
      crossroad_10.get.neighborsList.length must be (4)
      crossroad_10.get.neighborsList must contain (Host("manhattan.upper_west_side.crossroad_04", "127.0.0.1", 2551))
      crossroad_10.get.neighborsList must contain (Host("manhattan.lower_east_side.crossroad_01", "127.0.0.4", 2554))
      crossroad_10.get.neighborsList must contain (Host("manhattan.lower_west_side.crossroad_04", "127.0.0.3", 2553))
      crossroad_10.get.neighborsList must contain (Host("manhattan.lower_west_side.crossroad_01", "127.0.0.3", 2553))

      val crossroad_11: Option[Crossroad] =
        lowerWestSide.get.crossroadsList.find(crossroad => crossroad.name == "manhattan.lower_west_side.crossroad_03")
      crossroad_11.isDefined must be (TRUE)
      crossroad_11.get.name must be ("manhattan.lower_west_side.crossroad_03")
      crossroad_11.get.coordinate.latitude must be (25)
      crossroad_11.get.coordinate.longitude must be (425)
      crossroad_11.get.neighborsList.length must be (2)
      crossroad_11.get.neighborsList must contain (Host("manhattan.lower_west_side.crossroad_01", "127.0.0.3", 2553))
      crossroad_11.get.neighborsList must contain (Host("manhattan.lower_west_side.crossroad_04", "127.0.0.3", 2553))

      val crossroad_12: Option[Crossroad] =
        lowerWestSide.get.crossroadsList.find(crossroad => crossroad.name == "manhattan.lower_west_side.crossroad_04")
      crossroad_12.isDefined must be (TRUE)
      crossroad_12.get.name must be ("manhattan.lower_west_side.crossroad_04")
      crossroad_12.get.coordinate.latitude must be (125)
      crossroad_12.get.coordinate.longitude must be (425)
      crossroad_12.get.neighborsList.length must be (3)
      crossroad_12.get.neighborsList must contain (Host("manhattan.lower_west_side.crossroad_02", "127.0.0.3", 2553))
      crossroad_12.get.neighborsList must contain (Host("manhattan.lower_east_side.crossroad_03", "127.0.0.4", 2554))
      crossroad_12.get.neighborsList must contain (Host("manhattan.lower_west_side.crossroad_03", "127.0.0.3", 2553))

      val lowerEastSide: Option[District] =
        city.get.districtsList.find(district => district.name == "manhattan.lower_east_side")
      lowerEastSide.isDefined must be (TRUE)
      lowerEastSide.get.name must be ("manhattan.lower_east_side")
      lowerEastSide.get.host.name must be ("manhattan.lower_east_side")
      lowerEastSide.get.host.address must be ("127.0.0.4")
      lowerEastSide.get.host.port must be (2554)

      val crossroad_13: Option[Crossroad] =
        lowerEastSide.get.crossroadsList.find(crossroad => crossroad.name == "manhattan.lower_east_side.crossroad_01")
      crossroad_13.isDefined must be (TRUE)
      crossroad_13.get.name must be ("manhattan.lower_east_side.crossroad_01")
      crossroad_13.get.coordinate.latitude must be (325)
      crossroad_13.get.coordinate.longitude must be (325)
      crossroad_13.get.neighborsList.length must be (4)
      crossroad_13.get.neighborsList must contain (Host("manhattan.upper_east_side.crossroad_03", "127.0.0.2", 2552))
      crossroad_13.get.neighborsList must contain (Host("manhattan.lower_east_side.crossroad_02", "127.0.0.4", 2554))
      crossroad_13.get.neighborsList must contain (Host("manhattan.lower_east_side.crossroad_03", "127.0.0.4", 2554))
      crossroad_13.get.neighborsList must contain (Host("manhattan.lower_west_side.crossroad_02", "127.0.0.3", 2553))

      val crossroad_14: Option[Crossroad] =
        lowerEastSide.get.crossroadsList.find(crossroad => crossroad.name == "manhattan.lower_east_side.crossroad_02")
      crossroad_14.isDefined must be (TRUE)
      crossroad_14.get.name must be ("manhattan.lower_east_side.crossroad_02")
      crossroad_14.get.coordinate.latitude must be (425)
      crossroad_14.get.coordinate.longitude must be (325)
      crossroad_14.get.neighborsList.length must be (3)
      crossroad_14.get.neighborsList must contain (Host("manhattan.upper_east_side.crossroad_04", "127.0.0.2", 2552))
      crossroad_14.get.neighborsList must contain (Host("manhattan.lower_east_side.crossroad_04", "127.0.0.4", 2554))

      val crossroad_15: Option[Crossroad] =
        lowerEastSide.get.crossroadsList.find(crossroad => crossroad.name == "manhattan.lower_east_side.crossroad_03")
      crossroad_15.isDefined must be (TRUE)
      crossroad_15.get.name must be ("manhattan.lower_east_side.crossroad_03")
      crossroad_15.get.coordinate.latitude must be (325)
      crossroad_15.get.coordinate.longitude must be (425)
      crossroad_15.get.neighborsList.length must be (3)
      crossroad_15.get.neighborsList must contain (Host("manhattan.lower_east_side.crossroad_01", "127.0.0.4", 2554))
      crossroad_15.get.neighborsList must contain (Host("manhattan.lower_east_side.crossroad_04", "127.0.0.4", 2554))
      crossroad_15.get.neighborsList must contain (Host("manhattan.lower_west_side.crossroad_04", "127.0.0.3", 2553))

      val crossroad_16: Option[Crossroad] =
        lowerEastSide.get.crossroadsList.find(crossroad => crossroad.name == "manhattan.lower_east_side.crossroad_04")
      crossroad_16.isDefined must be (TRUE)
      crossroad_16.get.name must be ("manhattan.lower_east_side.crossroad_04")
      crossroad_16.get.coordinate.latitude must be (425)
      crossroad_16.get.coordinate.longitude must be (425)
      crossroad_16.get.neighborsList.length must be (2)
      crossroad_16.get.neighborsList must contain (Host("manhattan.lower_east_side.crossroad_02", "127.0.0.4", 2554))
      crossroad_16.get.neighborsList must contain (Host("manhattan.lower_east_side.crossroad_03", "127.0.0.4", 2554))
    }

    "correctly find the tcp port given the district name" in {
      val findTcpPort = PrivateMethod[Int]('findTcpPort)
      val unmarshaller: XMLUnmarshaller = new XMLUnmarshaller(configurationFile)

      unmarshaller.invokePrivate(findTcpPort("manhattan.upper_west_side")) must be (2551)
      unmarshaller.invokePrivate(findTcpPort("manhattan.upper_east_side")) must be (2552)
      unmarshaller.invokePrivate(findTcpPort("manhattan.lower_west_side")) must be (2553)
      unmarshaller.invokePrivate(findTcpPort("manhattan.lower_east_side")) must be (2554)
    }

    "create citizen objects according to the configuration file" in {
      val unmarshaller: XMLUnmarshaller = new XMLUnmarshaller(configurationFile)
      val citizensList: Vector[Citizen] = unmarshaller.citizensList
      var citizen: Option[Citizen] = None

      citizensList.count(citizen => citizen.vehicle == Vehicles.BUS)
      citizensList.count(citizen => citizen.vehicle == Vehicles.CAR)
      citizensList.count(citizen => citizen.vehicle == Vehicles.PAWN)
      citizensList.length must be (8)

      citizen = citizensList.find(citizen => citizen.name == "Jacob")
      citizen.isDefined must be (TRUE)
      citizen.get.vehicle must be (Vehicles.PAWN)
      citizen.get.addressBook.contains("manhattan.upper_west_side.crossroad_01") must be (TRUE)
      citizen.get.addressBook.contains("manhattan.lower_east_side.crossroad_02") must be (TRUE)
      citizen.get.addressBook.get("HOME").isDefined must be (TRUE)
      citizen.get.addressBook.get("WORK").isDefined must be (TRUE)
      citizen.get.addressBook.get("HOME").get must be ("manhattan.upper_west_side.crossroad_01")
      citizen.get.addressBook.get("WORK").get must be ("manhattan.lower_east_side.crossroad_02")

      citizen = citizensList.find(citizen => citizen.name == "Michael")
      citizen.isDefined must be (TRUE)
      citizen.get.vehicle must be (Vehicles.PAWN)
      citizen.get.addressBook.contains("manhattan.upper_east_side.crossroad_03") must be (TRUE)
      citizen.get.addressBook.contains("manhattan.upper_east_side.crossroad_02") must be (TRUE)
      citizen.get.addressBook.get("HOME").isDefined must be (TRUE)
      citizen.get.addressBook.get("WORK").isDefined must be (TRUE)
      citizen.get.addressBook.get("HOME").get must be ("manhattan.upper_east_side.crossroad_03")
      citizen.get.addressBook.get("WORK").get must be ("manhattan.upper_east_side.crossroad_02")

      citizen = citizensList.find(citizen => citizen.name == "Joshua")
      citizen.isDefined must be (TRUE)
      citizen.get.vehicle must be (Vehicles.CAR)
      citizen.get.addressBook.contains("manhattan.lower_west_side.crossroad_03") must be (TRUE)
      citizen.get.addressBook.contains("manhattan.lower_east_side.crossroad_04") must be (TRUE)
      citizen.get.addressBook.get("HOME").isDefined must be (TRUE)
      citizen.get.addressBook.get("WORK").isDefined must be (TRUE)
      citizen.get.addressBook.get("HOME").get must be ("manhattan.lower_west_side.crossroad_03")
      citizen.get.addressBook.get("WORK").get must be ("manhattan.lower_east_side.crossroad_04")

      citizen = citizensList.find(citizen => citizen.name == "Matthew")
      citizen.isDefined must be (TRUE)
      citizen.get.vehicle must be (Vehicles.CAR)
      citizen.get.addressBook.contains("manhattan.upper_west_side.crossroad_04") must be (TRUE)
      citizen.get.addressBook.contains("manhattan.lower_east_side.crossroad_04") must be (TRUE)
      citizen.get.addressBook.get("HOME").isDefined must be (TRUE)
      citizen.get.addressBook.get("WORK").isDefined must be (TRUE)
      citizen.get.addressBook.get("HOME").get must be ("manhattan.upper_west_side.crossroad_04")
      citizen.get.addressBook.get("WORK").get must be ("manhattan.lower_east_side.crossroad_04")

      citizen = citizensList.find(citizen => citizen.name == "Andrew")
      citizen.isDefined must be (TRUE)
      citizen.get.vehicle must be (Vehicles.BUS)
      citizen.get.addressBook.contains("manhattan.upper_east_side.crossroad_01") must be (TRUE)
      citizen.get.addressBook.contains("manhattan.lower_east_side.crossroad_01") must be (TRUE)
      citizen.get.addressBook.get("HOME").isDefined must be (TRUE)
      citizen.get.addressBook.get("WORK").isDefined must be (TRUE)
      citizen.get.addressBook.get("HOME").get must be ("manhattan.upper_east_side.crossroad_01")
      citizen.get.addressBook.get("WORK").get must be ("manhattan.lower_east_side.crossroad_01")

      citizen = citizensList.find(citizen => citizen.name == "Alexander")
      citizen.isDefined must be (TRUE)
      citizen.get.vehicle must be (Vehicles.BUS)
      citizen.get.addressBook.contains("manhattan.lower_west_side.crossroad_01") must be (TRUE)
      citizen.get.addressBook.contains("manhattan.lower_east_side.crossroad_04") must be (TRUE)
      citizen.get.addressBook.get("HOME").isDefined must be (TRUE)
      citizen.get.addressBook.get("WORK").isDefined must be (TRUE)
      citizen.get.addressBook.get("HOME").get must be ("manhattan.lower_west_side.crossroad_01")
      citizen.get.addressBook.get("WORK").get must be ("manhattan.lower_east_side.crossroad_04")

      citizen = citizensList.find(citizen => citizen.name == "Nicholas")
      citizen.isDefined must be (TRUE)
      citizen.get.vehicle must be (Vehicles.BUS)
      citizen.get.addressBook.contains("manhattan.upper_west_side.crossroad_04") must be (TRUE)
      citizen.get.addressBook.contains("manhattan.upper_east_side.crossroad_04") must be (TRUE)
      citizen.get.addressBook.get("HOME").isDefined must be (TRUE)
      citizen.get.addressBook.get("WORK").isDefined must be (TRUE)
      citizen.get.addressBook.get("HOME").get must be ("manhattan.upper_west_side.crossroad_04")
      citizen.get.addressBook.get("WORK").get must be ("manhattan.upper_east_side.crossroad_04")

      citizen = citizensList.find(citizen => citizen.name == "Tyler")
      citizen.isDefined must be (TRUE)
      citizen.get.vehicle must be (Vehicles.BUS)
      citizen.get.addressBook.contains("manhattan.lower_east_side.crossroad_01") must be (TRUE)
      citizen.get.addressBook.contains("manhattan.upper_west_side.crossroad_01") must be (TRUE)
      citizen.get.addressBook.get("HOME").isDefined must be (TRUE)
      citizen.get.addressBook.get("WORK").isDefined must be (TRUE)
      citizen.get.addressBook.get("HOME").get must be ("manhattan.lower_east_side.crossroad_01")
      citizen.get.addressBook.get("WORK").get must be ("manhattan.upper_west_side.crossroad_01")
    }

    "create vehicle objects according to the configuration file" in {
      val unmarshaller: XMLUnmarshaller = new XMLUnmarshaller(configurationFile)
      val vehiclesList: Vector[Vehicle] = unmarshaller.vehiclesList

      vehiclesList.length must be (5)

      vehiclesList.foreach{
        case bus: Bus =>
          bus.capacity must be (3)
          bus.run must be ("around_town")
          bus.stopsList.length must be (8)
          bus.stopsList must contain ("manhattan.upper_west_side.crossroad_01")
          bus.stopsList must contain ("manhattan.upper_west_side.crossroad_04")
          bus.stopsList must contain ("manhattan.upper_east_side.crossroad_01")
          bus.stopsList must contain ("manhattan.upper_east_side.crossroad_04")
          bus.stopsList must contain ("manhattan.lower_east_side.crossroad_04")
          bus.stopsList must contain ("manhattan.lower_east_side.crossroad_01")
          bus.stopsList must contain ("manhattan.lower_west_side.crossroad_04")
          bus.stopsList must contain ("manhattan.lower_west_side.crossroad_01")

        case car: Car =>
          car.driver.name must fullyMatch regex "(Joshua|Matthew)".r

        case pawn: Pawn =>
          pawn.name must fullyMatch regex "(Jacob|Michael)".r
      }
    }
  }
}