package org.citysimulator.core.configuration

import java.io.{File, FileNotFoundException, InputStream, InputStreamReader}

import javax.xml.XMLConstants
import javax.xml.transform.Source
import javax.xml.transform.stream.StreamSource
import javax.xml.validation.{Schema, SchemaFactory, Validator}

import org.citysimulator.core.business.citizen.Citizen
import org.citysimulator.core.business.map._
import org.citysimulator.core.business.vehicle._
import org.citysimulator.core.business.vehicle.Vehicles.Vehicles
import org.citysimulator.core.exception.ConfigurationException
import org.citysimulator.core.generator.IDGenerator
import org.citysimulator.core.network.Host

import scala.collection.mutable.ArrayBuffer
import scala.xml._

/**
 * Implements the operations that [[Unmarshaller]] trait specify interfacing with an XML file
 *
 * @param path [[String]] path of the configuration file
 */
class XMLUnmarshaller(path: String) extends Unmarshaller {
  protected val root: Elem = validate()
  protected val components: Int = getComponents

  /**
   * Get the IP address of the server that host's the district
   *
   * @param name [[String]] name of the district
   * @return Return the [[String]] IP address of the server that host the district
   */
  protected def findHostAddress(name: String): String = {
    ((root \\ "configuration" \ "city" \ "district").filter(districtNode => {
      districtNode.attribute("name").get.text == name
    }) \ "@hostname").text
  }

  /**
   * Get the TCP port on which the server that host the district will listen for incoming connections
   *
   * @param name [[String]] name of the district
   * @return Return the [[Int]] tcp port of the server that host the district
   */
  protected def findTcpPort(name: String): Int = {
    ((root \\ "configuration" \ "city" \ "district").filter(districtNode => {
      districtNode.attribute("name").get.text == name
    }) \ "@port").text.toInt
  }

  /**
   * Compute the number of critical components that constitute the system
   *
   * @return Return the [[Int]] number of components that the system build
   */
  protected def getComponents: Int = {
    (root \\ "configuration" \ "population" \ "citizen").length +
      (root \\ "configuration" \ "transport" \ "bus").length
  }

  /**
   * Validate the XML configuration file, with XML Schema grammar, before load and start the parsing phase
   *
   * @return Return the root [[Elem]] of the configuration file
   */
  protected def validate(): Elem = {
    try {
      //Open a stream towards the XML configuration file
      val xmlFile: File = new File(path)
      val xmlSource: Source = new StreamSource(xmlFile)

      //Open a stream towards the XML Schema grammar
      val xsdStream: InputStream = getClass.getResourceAsStream("/xml/grammars/city.xsd")
      val xsdReader: InputStreamReader = new InputStreamReader(xsdStream)
      val xsdSource: StreamSource = new StreamSource(xsdReader)

      //Load the schema grammar
      val schemaFactory: SchemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
      val schema: Schema = schemaFactory.newSchema(xsdSource)

      //Build the validation with the specified grammar
      val validator: Validator = schema.newValidator()

      //Validate the configuration file
      validator.validate(xmlSource)

      //Validation successful so we load the file in memory
      XML.loadFile(path)
    } catch {
      case exception: FileNotFoundException =>
        throw ConfigurationException("No file founded in the specified location. Check before retry the load")

      case exception: SAXException =>
        throw ConfigurationException(s"The file was not comply with the grammar. (${exception.getMessage})")
    }
  }

  /**
   * Get a [[Vector]] with the cities that the system must build during the bootstrap phase
   *
   * @return Return a [[Vector]] of [[City]] that the system must build
   */
  override def citiesList: Vector[City] = {
    val citiesList: ArrayBuffer[City] = ArrayBuffer.empty[City]

    (root \\ "configuration" \ "city").foreach(cityNode => {
      val name: String = cityNode.attribute("name").get.text

      val host: String = cityNode.attribute("hostname").get.text
      val port: Int = cityNode.attribute("port").get.text.toInt
      val server: Host = new Host(name, host, port)

      val districtsList: ArrayBuffer[District] = ArrayBuffer.empty[District]

      (cityNode \ "district").foreach(districtNode => {
        val name: String = districtNode.attribute("name").get.text

        val host: String = districtNode.attribute("hostname").get.text
        val port: Int = districtNode.attribute("port").get.text.toInt
        val server: Host = new Host(name, host, port)

        val crossroadsList: ArrayBuffer[Crossroad] = ArrayBuffer.empty[Crossroad]

        (districtNode \ "crossroad").foreach(crossroadNode => {
          val name: String = crossroadNode.attribute("name").get.text

          val coordinateNode: Node = crossroadNode.child.filter(child => child.label == "coordinate").head
          val latitude: Int = coordinateNode.child.filter(child => child.label == "latitude").head.text.toInt
          val longitude: Int = coordinateNode.child.filter(child => child.label == "longitude").head.text.toInt
          val coordinate: Coordinate = new Coordinate(latitude, longitude)

          val neighborsList: ArrayBuffer[Host] = ArrayBuffer.empty[Host]

          (crossroadNode \ "neighbors" \ "address").foreach(addressNode => {
            val name: String = addressNode.attribute("ref").get.text

            val district: String = name.substring(0, name.lastIndexOf('.'))
            val host: String = findHostAddress(district)
            val port: Int = findTcpPort(district)

            neighborsList += new Host(name, host, port)
          })

          crossroadsList += new Crossroad(name, coordinate, neighborsList.toVector)
        })

        districtsList += new District(name, server, crossroadsList.toVector)
      })

      citiesList += new City(name, server, districtsList.toVector)
    })

    citiesList.toVector
  }

  /**
   * Get a [[Vector]] with the citizen specified in the configuration file
   *
   * @return Return a [[Vector]] of [[Citizen]] that live in the city
   */
  override def citizensList: Vector[Citizen] = {
    val citizensList: ArrayBuffer[Citizen] = ArrayBuffer.empty[Citizen]

    (root \\ "configuration" \ "population" \ "citizen").foreach(citizenNode => {
      val id: String = IDGenerator.generateKey(components)
      val name: String = citizenNode.attribute("name").get.text

      val vehicle: Vehicles = {
        citizenNode.attribute("vehicle").get.text match {
          case "bus" => Vehicles.BUS
          case "car" => Vehicles.CAR
          case "pawn" => Vehicles.PAWN
        }
      }

      val citizen: Citizen = new Citizen(id, name, vehicle)

      (citizenNode \ "addressBook" \ "address").foreach(addressNode => {
        val key: String = addressNode.attribute("key").get.text
        val address: String = addressNode.attribute("ref").get.text

        citizen.addressBook.insert(key, address)
      })

      citizensList += citizen
    })

    citizensList.toVector
  }

  /**
   * Get a [[Vector]] with the vehicles that will travelling in the system
   *
   * @return Return a [[Vector]] of [[Vehicle]] that will travel in the system
   */
  override def vehiclesList: Vector[Vehicle] = {
    val citizensList: Vector[Citizen] = this.citizensList
    val vehiclesList: ArrayBuffer[Vehicle] = ArrayBuffer.empty[Vehicle]

    (root \\ "configuration" \ "transport" \ "bus").foreach(busNode => {
      val id: String = IDGenerator.generateKey(components)
      val run: String = busNode.attribute("run").get.text
      val capacity: Int = busNode.attribute("capacity").get.text.toInt
      val stopsList: ArrayBuffer[String] = ArrayBuffer.empty[String]

      (busNode \ "stops" \ "address").foreach(addressNode => {
        stopsList += addressNode.attribute("ref").get.text
      })

      vehiclesList += new Bus(id, run, capacity, stopsList.toVector)
    })

    citizensList.filter(citizen => citizen.vehicle != Vehicles.BUS).foreach(citizen => {
      citizen.vehicle match {
        case Vehicles.CAR => vehiclesList += new Car(IDGenerator.generateKey(components), citizen)
        case Vehicles.PAWN => vehiclesList += new Pawn(citizen)
      }
    })

    vehiclesList.toVector
  }
}