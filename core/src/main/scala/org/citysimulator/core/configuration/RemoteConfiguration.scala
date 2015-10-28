package org.citysimulator.core.configuration

import com.typesafe.config.{Config, ConfigFactory}

import org.citysimulator.core.network.Host

/**
 * It is able to configure the underlying framework to operate with remote components
 */
object RemoteConfiguration {

  /**
   * Configure the underlying framework to operate with remote components over the TCP protocol
   *
   * @param host [[Host]] information about the server that host a part of the system
   * @param mergeable Mergeable [[Config]]
   * @return Return a [[Config]] objects correctly configured for operate with remote components
   */
  def configure(host: Host, mergeable: Config = ConfigFactory.defaultApplication()): Config = {
    val provider: String = "akka.actor.provider = akka.remote.RemoteActorRefProvider"
    val transport: String = """akka.remote.enabled-transports = ["akka.remote.netty.tcp"]"""
    val hostname: String = s"""akka.remote.netty.tcp.hostname = ${host.address}"""
    val tcpPort: String = s"""akka.remote.netty.tcp.port = ${host.port}"""

    val configurationsString =
      s"""$provider
         |$transport
         |$hostname
         |$tcpPort
       """.stripMargin

    ConfigFactory.parseString(configurationsString).withFallback(mergeable)
  }
}