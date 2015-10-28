package org.citysimulator.core.configuration

/**
 * Factory object that is able to create correctly and configured unmarshaller objects to parse configuration files
 */
object UnmarshallerFactory {

  /**
   * Build an unmarshaller that is able to read and parse a configuration file written in the XML language
   *
   * @param path [[String]] path of the configuration file
   * @return Return an [[XMLUnmarshaller]] that is able to parse the configuration file
   */
  def newXmlUnmarshaller(path: String): XMLUnmarshaller = new XMLUnmarshaller(path)
}