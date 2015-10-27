package org.citysimulator.core.exception

/**
 * Exception thrown if are found errors during the loading phase of the configuration file
 *
 * @param message [[String]] description of the problem found
 */
case class ConfigurationException(message: String) extends RuntimeException(message)