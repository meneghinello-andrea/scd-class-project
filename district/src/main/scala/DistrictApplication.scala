import akka.actor.{ActorSystem, Props}

import com.typesafe.config.{Config, ConfigFactory}

import org.citysimulator.core.actor.StarterActor
import org.citysimulator.core.configuration.RemoteConfiguration
import org.citysimulator.core.network.Host

import scala.io.Source

/**
 *
 */
object DistrictApplication {

  def main(args: Array[String]) {
    try {
      val configuration: scala.collection.mutable.Map[String, String] =
        scala.collection.mutable.Map.empty[String, String]

      if (args.length == 1) {
        //Get files paths
        val networkFile: Option[String] = args.headOption

        //Load the information about the network file
        for (line <- Source.fromFile(networkFile.get).getLines()) {
          val key: Option[String] = line.split('=').headOption
          val value: Option[String] = line.split('=').lastOption

          if (key.isDefined && value.isDefined) {
            configuration += (key.get -> value.get)
          }
        }

        //Prepare network information for the actor system
        val host: Host = new Host(
          configuration.get("district.name").get,
          configuration.get("district.host").get,
          configuration.get("district.port").get.toInt)

        //Create the actor system
        val name: String = configuration.get("district.name").get.split('.').last
        val systemConfiguration: Config = RemoteConfiguration.configure(host, ConfigFactory.load())
        val actorSystem: ActorSystem = ActorSystem(name, systemConfiguration)

        //Create the city actor
        actorSystem.actorOf(Props(new StarterActor(
          configuration.get("city.name").get,
          configuration.get("city.host").get,
          configuration.get("city.port").get.toInt
        )), "starter")
      }
    } catch {
      case ex: Exception => println(s"[EXCEPTION]: ${ex.getMessage}")
    }
  }
}