import akka.actor.{ActorRef, ActorSystem, Props}

import com.typesafe.config.{Config, ConfigFactory}

import org.citysimulator.core.actor.CityActor
import org.citysimulator.core.configuration.RemoteConfiguration
import org.citysimulator.core.message.Shutdown.StartShutdown
import org.citysimulator.core.network.Host

import scala.io.{StdIn, Source}

/**
 *
 */
object CityApplication {

  def main(args: Array[String]) {
    try {
      val configuration: scala.collection.mutable.Map[String, String] =
        scala.collection.mutable.Map.empty[String, String]

      if (args.length == 2) {
        //Get files paths
        val networkFile: Option[String] = args.headOption
        val configFile: Option[String] = args.lastOption

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
          configuration.get("city.name").get,
          configuration.get("city.host").get,
          configuration.get("city.port").get.toInt)

        //Create the actor system
        val name: String = configuration.get("city.name").get
        val systemConfiguration: Config = RemoteConfiguration.configure(host, ConfigFactory.load())
        val actorSystem: ActorSystem = ActorSystem(name, systemConfiguration)

        //Create the city actor
        val cityActor: ActorRef = actorSystem.actorOf(Props(new CityActor(configFile.get)), "city")

        var exit: Boolean = false
        while (!exit) {
          println("Type 'exit' to start the shutdown:")
          val exitCode: String = StdIn.readLine()

          if (exitCode.contains("exit")) {
            cityActor ! StartShutdown()
            exit = !exit
          }
        }
      }
    } catch {
      case ex: Exception => println(s"[EXCEPTION]: ${ex.getMessage}")
    }
  }
}