package org.citysimulator.core.business.map

import org.citysimulator.core.network.Host

/**
 * Contains the information about a city district
 *
 * @param name [[String]] name of the district
 * @param host [[Host]] information about the server that host the district
 * @param crossroadsList [[Vector]] of [[Crossroad]] administrated by the district
 */
case class District(name: String, host: Host, crossroadsList: Vector[Crossroad])