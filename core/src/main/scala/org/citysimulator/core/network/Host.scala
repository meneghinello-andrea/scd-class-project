package org.citysimulator.core.network

/**
 * Contains information about the server in the network that host a part of the system
 *
 * @param name [[String]] name of the part hosted in the server
 * @param address [[String]] IP address of the server that host a part of the system
 * @param port [[Int]] TCP network port on which the server listen for incoming connections
 */
case class Host(name: String, address: String, port: Int)