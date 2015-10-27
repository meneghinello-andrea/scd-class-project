package org.citysimulator.core.business.citizen

import org.citysimulator.core.business.citizen.CitizenStatus.CitizenStatus
import org.citysimulator.core.business.vehicle.Vehicles.Vehicles

/**
 * Contains the information for a [[Citizen]] that live in the city
 *
 * @param id [[String]] identifier of the [[Citizen]]
 * @param name [[String]] name of the [[Citizen]]
 * @param vehicle Vehicle used by the citizen when it travels in the city
 * @param addressBook [[AddressBook]] where the [[Citizen]] save important address
 * @param status Information on what about the citizen is performing
 */
case class Citizen(id: String,
                   name: String,
                   vehicle: Vehicles,
                   addressBook: AddressBook,
                   var status: CitizenStatus = CitizenStatus.SLEEPING)