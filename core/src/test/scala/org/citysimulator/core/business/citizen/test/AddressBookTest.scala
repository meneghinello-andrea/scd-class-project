package org.citysimulator.core.business.citizen.test

import org.citysimulator.core.business.citizen.AddressBook
import org.citysimulator.core.constants.test.TestConstants._

import org.scalatest.{MustMatchers, WordSpec}

/**
 * Test the [[AddressBook]] component's operation in common use scenarios
 */
class AddressBookTest extends WordSpec with MustMatchers {
  protected val home: String = "home"
  protected val work: String = "work"

  protected val homeAddress: String = "city.district.crossroad_01"
  protected val workAddress: String = "city.district.crossroad_02"

  "The address book" must {
    "be empty when it is created" in {
      val addressBook: AddressBook = new AddressBook()

      addressBook.count must be (0)
      addressBook.isEmpty must be (TRUE)
      addressBook.keys must be (Set.empty[String])
    }

    "permit the insertion of an address with the associated key" in {
      val addressBook: AddressBook = new AddressBook()

      addressBook.contains(homeAddress) must be (FALSE)
      addressBook.keys must not contain home

      addressBook.insert(home, homeAddress)

      addressBook.count must be (1)
      addressBook.isEmpty must be (FALSE)
      addressBook.keys must contain (home.toUpperCase)
      addressBook.contains(homeAddress) must be (TRUE)
    }

    "get the address associated to a key" in {
      val addressBook: AddressBook = new AddressBook()

      addressBook.insert(home, homeAddress)

      addressBook.get(home).isDefined must be (TRUE)
      addressBook.get(home).get must be (homeAddress)
    }

    "be empty when it is cleaned" in {
      val addressBook: AddressBook = new AddressBook()

      addressBook.count must be (0)
      addressBook.isEmpty must be (TRUE)

      addressBook.insert(home, homeAddress)
      addressBook.insert(work, workAddress)

      addressBook.count must be (2)
      addressBook.isEmpty must be (FALSE)

      addressBook.clear()

      addressBook.count must be (0)
      addressBook.isEmpty must be (TRUE)
    }

    "update the entry when an address with an existing key is inserted" in {
      val addressBook: AddressBook = new AddressBook()

      addressBook.insert(home, homeAddress)

      addressBook.contains(homeAddress)
      addressBook.count must be (1)

      addressBook.insert(home, workAddress)

      addressBook.contains(homeAddress) must be (FALSE)
      addressBook.contains(workAddress) must be (TRUE)
      addressBook.count must be (1)
    }

    "get no data if it not contains the requested key" in {
      val addressBook: AddressBook = new AddressBook()

      addressBook.insert(home, homeAddress)

      addressBook.contains(workAddress) must be (FALSE)
      addressBook.get(work).isDefined must be (FALSE)
    }

    "permit the deletion of an address and the associated key" in {
      val addressBook: AddressBook = new AddressBook()

      addressBook.insert(home, homeAddress)

      addressBook.contains(homeAddress)
      addressBook.keys must contain (home.toUpperCase)

      addressBook.remove(home)

      addressBook.contains(homeAddress) must be (FALSE)
      addressBook.keys must not contain home.toUpperCase
    }

    "remain unchanged when try to remove an address not present" in {
      val addressBook: AddressBook = new AddressBook()

      addressBook.insert(home, homeAddress)

      addressBook.contains(homeAddress) must be (TRUE)
      addressBook.count must be (1)
      addressBook.isEmpty must be (FALSE)
      addressBook.get(home).isDefined must be (TRUE)
      addressBook.get(home).get must be (homeAddress)

      addressBook.remove(work)

      addressBook.contains(homeAddress) must be (TRUE)
      addressBook.count must be (1)
      addressBook.isEmpty must be (FALSE)
      addressBook.get(home).isDefined must be (TRUE)
      addressBook.get(home).get must be (homeAddress)
    }
  }
}