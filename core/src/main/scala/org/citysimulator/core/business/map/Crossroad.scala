package org.citysimulator.core.business.map

import org.citysimulator.core.network.Host

/**
 * Contains information about a city crossroad
 *
 * @param name [[String]] name of the crossroad
 * @param coordinate Geographical [[Coordinate]] of the crossroad
 * @param neighborsList [[Vector]] with the [[Host]] information to contact neighbor crossroad
 */
case class Crossroad(name: String, coordinate: Coordinate, neighborsList: Vector[Host])