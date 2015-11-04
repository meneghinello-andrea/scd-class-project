package org.citysimulator.core.business.citizen

/**
 * Memorize the addresses, associated to a key, where the [[Citizen]] that live in the city can perform their
 * programmed tasks
 */
case class AddressBook() {
  private val book: scala.collection.mutable.Map[String, String] = scala.collection.mutable.Map.empty[String, String]

  /**
   * Check if an address is already memorized
   *
   * @param address [[String]] address
   * @return Return true if the address is already registered, false otherwise
   */
  def contains(address: String): Boolean = book.exists(entry => entry._2 == address)

  /**
   * Get the number of memorized addresses
   *
   * @return Return the [[Int]] number of addresses actually memorized
   */
  def count: Int = book.size

  /**
   * Clear the address book
   */
  def clear(): Unit = book.clear()

  /**
   * Get the address associated to a key
   *
   * @param key [[String]] key associated to the address
   * @return Return an [[Option]] with the associated address
   */
  def get(key: String): Option[String] = book.get(key.toUpperCase)

  /**
   * Insert the address if the key is not already memorized otherwise update the address associated to the key
   *
   * @param key [[String]] key associated to the address
   * @param address [[String]] address
   */
  def insert(key: String, address: String): Unit = book += (key.toUpperCase -> address)

  /**
   * Check if the address book is empty
   *
   * @return Return true if it is empty, false otherwise
   */
  def isEmpty: Boolean = book.isEmpty

  /**
   * Get an [[Iterable]] collection with the keys memorized
   *
   * @return Return an [[Iterable]] collection with the keys memorized
   */
  def keys: Iterable[String] = book.keys

  /**
   * Remove an address and its key from the address book
   *
   * @param key [[String]] key associated to the address
   */
  def remove(key: String): Unit = book.remove(key.toUpperCase)
}