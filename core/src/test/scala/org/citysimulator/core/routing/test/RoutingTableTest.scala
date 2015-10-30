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
      val addresses = PrivateMethod[Iterable[String]]('addresses)
      val get = PrivateMethod[Option[RoutingEntry]]('get)

      routingTable.count must be(0)
      routingTable.isEmpty must be(TRUE)

      val iterator: Iterator[String] = routingTable.invokePrivate(addresses()).iterator
      var counter = 0
      while (iterator.hasNext) {
        val address: String = iterator.next()
        val entry: Option[RoutingEntry] = routingTable.invokePrivate(get(address))

        counter += 1
        address must fullyMatch regex """address.none.\d*""".r
        entry.isDefined must be(TRUE)
        entry.get.address must fullyMatch regex """address.none.\d*""".r
        entry.get.nextHop.isDefined must be(FALSE)
      }

      counter must be(routingTable.capacity)
    }

    "permit the insertion of a new route found by the algorithm" in {
      val routingTable: RoutingTable = new RoutingTable(5)
      val addresses = PrivateMethod[Iterable[String]]('addresses)
      val get = PrivateMethod[Option[RoutingEntry]]('get)

      val address: String = "manhattan.upper_west_side.crossroad_01"
      val nextHop: TestProbe = TestProbe()
      routingTable.insert(address, nextHop.ref)

      routingTable.count must be(1)
      routingTable.isEmpty must be(FALSE)

      val entry: Option[RoutingEntry] = routingTable.invokePrivate(get(address))
      entry.isDefined must be(TRUE)
      entry.get.address must be(address)
      entry.get.nextHop.isDefined must be(TRUE)
      entry.get.nextHop.get must be(nextHop.ref)

      var counter = 0
      val iterator: Iterator[String] = routingTable.invokePrivate(addresses()).iterator
      while (iterator.hasNext) {
        counter += 1
        iterator.next()
      }
      counter must be(routingTable.capacity)
    }

    "get the next hop information about a key" in {
      val routingTable: RoutingTable = new RoutingTable(5)
      val get = PrivateMethod[Option[RoutingEntry]]('get)

      val address: String = "manhattan.upper_west_side.crossroad_01"
      val nextHop: TestProbe = TestProbe()
      routingTable.insert(address, nextHop.ref)

      routingTable.count must be(1)
      routingTable.isEmpty must be(FALSE)

      val entry: Option[ActorRef] = routingTable.lookup(address)
      entry.isDefined must be(TRUE)
      entry.get must be(nextHop.ref)
    }

    "be empty when it is cleaned" in {
      val routingTable: RoutingTable = new RoutingTable(5)
      val addresses = PrivateMethod[Iterable[String]]('addresses)
      val get = PrivateMethod[Option[RoutingEntry]]('get)

      routingTable.count must be(0)
      routingTable.isEmpty must be(TRUE)

      var iterator: Iterator[String] = routingTable.invokePrivate(addresses()).iterator
      var counter = 0
      while (iterator.hasNext) {
        val address: String = iterator.next()
        val entry: Option[RoutingEntry] = routingTable.invokePrivate(get(address))

        counter += 1
        address must fullyMatch regex """address.none.\d*""".r
        entry.isDefined must be(TRUE)
        entry.get.address must fullyMatch regex """address.none.\d*""".r
        entry.get.nextHop.isDefined must be(FALSE)
      }

      counter must be(routingTable.capacity)

      val address: String = "manhattan.upper_west_side.crossroad_01"
      val nextHop: TestProbe = TestProbe()
      routingTable.insert(address, nextHop.ref)

      routingTable.clear()

      routingTable.count must be(0)
      routingTable.isEmpty must be(TRUE)

      iterator = routingTable.invokePrivate(addresses()).iterator
      counter = 0
      while (iterator.hasNext) {
        val address: String = iterator.next()
        val entry: Option[RoutingEntry] = routingTable.invokePrivate(get(address))

        counter += 1
        address must fullyMatch regex """address.none.\d*""".r
        entry.isDefined must be(TRUE)
        entry.get.address must fullyMatch regex """address.none.\d*""".r
        entry.get.nextHop.isDefined must be(FALSE)
      }

      counter must be(routingTable.capacity)
    }

    "refresh the freshness of a route if it is used" in {
      val routingTable: RoutingTable = new RoutingTable(5)
      val get = PrivateMethod[Option[RoutingEntry]]('get)

      val address: String = "manhattan.upper_west_side.crossroad_01"
      val nextHop: TestProbe = TestProbe()
      routingTable.insert(address, nextHop.ref)

      val older: Long = routingTable.invokePrivate(get(address)).get.inserted
      Thread.sleep(1000)
      routingTable.refresh(address)
      val newer: Long = routingTable.invokePrivate(get(address)).get.inserted

      older must (be < newer)
    }

    "update the routing information" in {
      val routingTable: RoutingTable = new RoutingTable(5)
      val get = PrivateMethod[Option[RoutingEntry]]('get)

      val address: String = "manhattan.upper_west_side.crossroad_01"
      val nextHop_01: TestProbe = TestProbe()
      val nextHop_02: TestProbe = TestProbe()

      routingTable.insert(address, nextHop_01.ref)

      var entry: Option[ActorRef] = routingTable.lookup(address)
      entry.isDefined must be (TRUE)
      entry.get must be (nextHop_01.ref)

      routingTable.update(address, nextHop_02.ref)

      entry= routingTable.lookup(address)
      entry.isDefined must be (TRUE)
      entry.get must be (nextHop_02.ref)
    }
  }
}