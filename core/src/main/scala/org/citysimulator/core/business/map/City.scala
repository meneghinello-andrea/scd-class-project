package org.citysimulator.core.business.map

import org.citysimulator.core.network.Host

/**
 * Contains the information about a city
 *
 * @param name [[String]] name of the city
 * @param host [[Host]] information about the server that host the city
 * @param districtsList [[Vector]] of [[District]] administrated by the city
 */
case class City(name: String, host: Host, districtsList: Vector[District])