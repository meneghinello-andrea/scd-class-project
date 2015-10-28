package org.citysimulator.core.configuration.test

import com.typesafe.config.Config

import org.citysimulator.core.configuration.RemoteConfiguration
import org.citysimulator.core.network.Host

import org.scalatest.{MustMatchers, WordSpec}

/**
 * Test the [[RemoteConfiguration]] component's operation in common use scenarios
 */
class RemoteConfigurationTest extends WordSpec with MustMatchers {
  "Remote configuration" must {
    "setup correctly the underlying framework for operating with remote components" in {
      val host: Host = new Host("manhattan.upper_west_side", "97.123.54.78", 12345)

      val configuration: Config = RemoteConfiguration.configure(host)

      configuration.getAnyRef("akka.actor.provider") must be ("akka.remote.RemoteActorRefProvider")
      configuration.getAnyRefList("akka.remote.enabled-transports") must contain ("akka.remote.netty.tcp")
      configuration.getAnyRef("akka.remote.netty.tcp.hostname") must be ("97.123.54.78")
      configuration.getAnyRef("akka.remote.netty.tcp.port") must be (12345)
    }
  }
}