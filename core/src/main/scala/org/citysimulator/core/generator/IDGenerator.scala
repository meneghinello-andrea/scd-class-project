package org.citysimulator.core.generator

import scala.util.Random

/**
 * It is able to generate random keys used as identifier of critical components in the system. The generated keys
 * have a length that depends by the number of critical component that compose the system and it is composed by a
 * variable number of upper case/lower case chars and digits
 */
object IDGenerator {

  /**
   * Generate a random char in the specified interval
   *
   * @param lowerBound [[Int]] lower bound of the interval
   * @param upperBound [[Int]] upper bound of the interval
   * @return Return the generated char
   */
  protected def generateChar(lowerBound: Int, upperBound: Int): Char = {
    if (lowerBound < upperBound) {
      (Random.nextInt(upperBound - lowerBound) + lowerBound).toChar
    } else {
      (Random.nextInt(lowerBound - upperBound) + upperBound).toChar
    }
  }

  /**
   * Compute the key length based on the number of critical components in the system
   *
   * @param components [[Int]] number of critical components
   * @return Return the length of the key
   */
  protected def keyLength(components: Int): Int = 10 + (components % 7)

  def generateKey(components: Int = 9): String = {

    //Compute the key length
    val length: Int = keyLength(components)

    //Compute the number of chars type that compose the key
    val lowerCaseChars: Int = Random.nextInt(length)
    val upperCaseChars: Int = Random.nextInt(length - lowerCaseChars)
    val digitsChar: Int = length - (lowerCaseChars + upperCaseChars)

    //Create an empty key
    var key: Seq[Char] = Seq.empty[Char]

    //Generate the lower case chars that will compose the new key
    for (i <- 1 to lowerCaseChars) {
      key = key :+ generateChar(97, 122)
    }

    //Generate the upper case chars that will compose the new key
    for (i <- 1 to upperCaseChars) {
      key = key :+ generateChar(65, 90)
    }

    //Generate the digits chars that will compose the new key
    for (i <- 1 to digitsChar) {
      key = key :+ generateChar(48, 57)
    }

    //Shuffle the generated chars to lowering the probability of key conflict
    Random.shuffle(key)

    //Create the string with the generated key
    key.mkString
  }
}