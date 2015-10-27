package org.citysimulator.core.generator.test

import org.citysimulator.core.generator.IDGenerator

import org.scalatest.{MustMatchers, PrivateMethodTester, WordSpec}

/**
 * Test the [[IDGenerator]] component's operation in common use scenarios
 */
class IDGeneratorTest extends WordSpec with MustMatchers with PrivateMethodTester {
  "The ID generator component" must {
    "generate keys with the correct length based on the number of critical components present in the system" in {
      val keyLength = PrivateMethod[Int]('keyLength)

      IDGenerator.invokePrivate(keyLength(9)) must be (12)
      IDGenerator.invokePrivate(keyLength(20)) must be (16)
      IDGenerator.invokePrivate(keyLength(42)) must be (10)
      IDGenerator.invokePrivate(keyLength(73)) must be (13)
      IDGenerator.invokePrivate(keyLength(90)) must be (16)
      IDGenerator.invokePrivate(keyLength(100)) must be (12)
    }

    "generate chars in the specified interval" in {
      val generateChar = PrivateMethod[Char]('generateChar)

      //Check lower case chars interval
      for (1 <- 1 to 100000) {
        IDGenerator.invokePrivate(generateChar(97, 122)).toInt must (be >= 97 and be <= 122)
      }

      //Check upper case chars interval
      for (1 <- 1 to 100000) {
        IDGenerator.invokePrivate(generateChar(65, 90)).toInt must (be >= 65 and be <= 90)
      }

      //Check digits chars interval
      for (1 <- 1 to 100000) {
        IDGenerator.invokePrivate(generateChar(48, 57)).toInt must (be >= 48 and be <= 57)
      }
    }
  }
}