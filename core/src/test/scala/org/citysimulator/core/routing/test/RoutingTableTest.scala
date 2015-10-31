package org.citysimulator.core.routing.test

import akka.actor.{ActorRef, ActorSystem}
import akka.testkit.{TestKit, TestProbe}

import org.citysimulator.core.routing.{RoutingEntry, RoutingTable}
import org.citysimulator.core.constants.test.TestConstants._

import org.scalatest.{BeforeAndAfterAll, MustMatchers, PrivateMethodTester, WordSpecLike}

/**
 * Test the [[RoutingTable]] component's operation in common use scenarios
 */
class RoutingTableTest extends TestKit(ActorSystem("RoutingTableTest")) with WordSpecLike
                                                                        with MustMatchers
                                                                        with PrivateMethodTester
                                                                        with BeforeAndAfterAll {
  /**
   * Terminate the actor system after that all the test cases are been executed
   */
  override def afterAll(): Unit = system.terminate()

  "The routing table" must {
    "be empty when it is created" in {
      val routingTable: RoutingTable = new RoutingTable(5)

      routingTable.addresses must be (Seq.empty[String])
      routingTable.count must be (0)
      routingTable.isEmpty must be (TRUE)
    }

    "permit the insertion of a new route found by the algorithm" in {
      val routingTable: RoutingTable = new RoutingTable(5)
      val address: String = "manhattan.upper_west_side.crossroad_01"
      val nextHop: ActorRef = TestProbe().ref

      routingTable.addresses must be (Seq.empty[String])
      routingTable.count must be (0)
      routingTable.isEmpty must be (TRUE)

      routingTable.insert(address, nextHop)

      routingTable.addresses must contain (address)
      routingTable.count must be (1)
      routingTable.isEmpty must be (FALSE)
    }

    "not contain routing information over its capacity" in {
      val routingTable: RoutingTable = new RoutingTable(2)
      val address_01: String = "manhattan.upper_west_side.crossroad_01"
      val address_02: String = "manhattan.upper_west_side.crossroad_02"
      val address_03: String = "manhattan.upper_west_side.crossroad_03"
      val nextHop_01: ActorRef = TestProbe().ref
      val nextHop_02: ActorRef = TestProbe().ref
      val nextHop_03: ActorRef = TestProbe().ref

      routingTable.addresses must be (Seq.empty[String])
      routingTable.count must be (0)
      routingTable.isEmpty must be (TRUE)

      routingTable.insert(address_01, nextHop_01)

      routingTable.count must be (1)
      routingTable.isEmpty must be (FALSE)

      routingTable.insert(address_02, nextHop_02)

      routingTable.count must be (2)
      routingTable.isEmpty must be (FALSE)

      routingTable.insert(address_03, nextHop_03)

      routingTable.count must be (2)
      routingTable.isEmpty must be (FALSE)
    }

    "replace the older route information when there is no available place" in {
      val routingTable: RoutingTable = new RoutingTable(2)
      val address_01: String = "manhattan.upper_west_side.crossroad_01"
      val address_02: String = "manhattan.upper_west_side.crossroad_02"
      val address_03: String = "manhattan.upper_west_side.crossroad_03"
      val nextHop_01: ActorRef = TestProbe().ref
      val nextHop_02: ActorRef = TestProbe().ref
      val nextHop_03: ActorRef = TestProbe().ref

      routingTable.addresses must be (Seq.empty[String])

      routingTable.insert(address_01, nextHop_01)

      routingTable.addresses must contain (address_01)

      Thread.sleep(500)
      routingTable.insert(address_02, nextHop_02)

      routingTable.addresses must contain (address_01)
      routingTable.addresses must contain (address_02)

      routingTable.insert(address_03, nextHop_03)

      routingTable.addresses must not contain address_01
      routingTable.addresses must contain (address_02)
      routingTable.addresses must contain (address_03)
    }
  }

  "get the next hop information about an address memorized" in {
    val routingTable: RoutingTable = new RoutingTable(5)
    val address: String = "manhattan.upper_west_side.crossroad_01"
    val nextHop: ActorRef = TestProbe().ref

    routingTable.insert(address, nextHop)

    routingTable.lookup(address).isDefined must be (TRUE)
    routingTable.lookup(address).get must be (nextHop)
  }

  "get no information about a destination if the address isn't memorized in it" in {
    val routingTable: RoutingTable = new RoutingTable(5)
    val address_01: String = "manhattan.upper_west_side.crossroad_01"
    val address_02: String = "manhattan.upper_west_side.crossroad_02"
    val nextHop_01: ActorRef = TestProbe().ref

    routingTable.insert(address_01, nextHop_01)

    routingTable.lookup(address_02).isDefined must be (FALSE)
  }

  "be empty when it is cleaned" in {
    val routingTable: RoutingTable = new RoutingTable(2)
    val address_01: String = "manhattan.upper_west_side.crossroad_01"
    val address_02: String = "manhattan.upper_west_side.crossroad_02"
    val nextHop_01: ActorRef = TestProbe().ref
    val nextHop_02: ActorRef = TestProbe().ref

    routingTable.insert(address_01, nextHop_01)
    routingTable.insert(address_02, nextHop_02)

    routingTable.count must be (2)
    routingTable.isEmpty must be (FALSE)

    routingTable.clear()

    routingTable.count must be (0)
    routingTable.isEmpty must be (TRUE)
  }

  "permit to refresh the freshness of a route information" in {
    val routingTable: RoutingTable = new RoutingTable(2)
    val address_01: String = "manhattan.upper_west_side.crossroad_01"
    val address_02: String = "manhattan.upper_west_side.crossroad_02"
    val address_03: String = "manhattan.upper_west_side.crossroad_03"
    val nextHop_01: ActorRef = TestProbe().ref
    val nextHop_02: ActorRef = TestProbe().ref
    val nextHop_03: ActorRef = TestProbe().ref

    routingTable.insert(address_01, nextHop_01)
    routingTable.insert(address_02, nextHop_02)

    routingTable.addresses must contain (address_01)
    routingTable.addresses must contain (address_02)

    //Thread.sleep(1000)
    routingTable.refresh(address_01)

    routingTable.addresses must contain (address_01)
    routingTable.addresses must contain (address_02)

    routingTable.insert(address_03, nextHop_03)

    routingTable.addresses must contain (address_01)
    routingTable.addresses must not contain address_02
    routingTable.addresses must contain (address_03)
  }

  "permit the deletion of a route information" in {
    val routingTable: RoutingTable = new RoutingTable(2)
    val address_01: String = "manhattan.upper_west_side.crossroad_01"
    val address_02: String = "manhattan.upper_west_side.crossroad_02"
    val nextHop_01: ActorRef = TestProbe().ref
    val nextHop_02: ActorRef = TestProbe().ref

    routingTable.count must be (0)
    routingTable.isEmpty must be (TRUE)

    routingTable.insert(address_01, nextHop_01)
    routingTable.insert(address_02, nextHop_02)

    routingTable.count must be (2)
    routingTable.isEmpty must be (FALSE)

    routingTable.remove(address_01)

    routingTable.addresses must not contain address_01
    routingTable.addresses must contain (address_02)
    routingTable.count must be (1)
    routingTable.isEmpty must be (FALSE)
  }

  "permit to update the information of a memorized route" in {
    val routingTable: RoutingTable = new RoutingTable(2)
    val address_01: String = "manhattan.upper_west_side.crossroad_01"
    val nextHop_01: ActorRef = TestProbe().ref
    val nextHop_02: ActorRef = TestProbe().ref

    routingTable.count must be (0)

    routingTable.insert(address_01, nextHop_01)

    routingTable.addresses must contain (address_01)
    routingTable.count must be (1)
    routingTable.lookup(address_01).isDefined must be (TRUE)
    routingTable.lookup(address_01).get must be (nextHop_01)

    routingTable.update(address_01, nextHop_02)

    routingTable.addresses must contain (address_01)
    routingTable.count must be (1)
    routingTable.lookup(address_01).isDefined must be (TRUE)

    routingTable.lookup(address_01).get must be (nextHop_02)
  }

  "insert the route information if the update operation found that the route does not exist" in {
    val routingTable: RoutingTable = new RoutingTable(2)
    val address_01: String = "manhattan.upper_west_side.crossroad_01"
    val nextHop_01: ActorRef = TestProbe().ref
    val nextHop_02: ActorRef = TestProbe().ref

    routingTable.count must be (0)

    routingTable.update(address_01, nextHop_01)

    routingTable.addresses must contain (address_01)
    routingTable.count must be (1)
    routingTable.lookup(address_01).isDefined must be (TRUE)
    routingTable.lookup(address_01).get must be (nextHop_01)
  }
}