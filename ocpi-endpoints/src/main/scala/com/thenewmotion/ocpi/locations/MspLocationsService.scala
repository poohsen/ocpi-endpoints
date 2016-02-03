package com.thenewmotion.ocpi.locations

import com.thenewmotion.ocpi.msgs.v2_0.Locations._
import scalaz._

trait MspLocationsService {

  def createLocation(cc: String, opId: String, loc: Location): LocationsError \/ Unit

  def addEvse(cc: String, opId: String, locId: String, evseId: String, evse: Evse): LocationsError \/ Unit

  def addConnector(cc: String, opId: String, locId: String, evseId: String, connId: String, connector: Connector): LocationsError \/ Unit

  def updateLocation(cc: String, opId: String, locPatch: LocationPatch): LocationsError \/ Unit

  def updateEvse(cc: String, opId: String, locId: String, evsePatch: EvsePatch): LocationsError \/ Unit

  def updateConnector(cc: String, opId: String, locId: String, evseId: String, connectorPatch: ConnectorPatch): LocationsError \/ Unit

  def location(cc: String, opId: String, locId: String): LocationsError \/ Location

  def evse(cc: String, opId: String, locId: String, evseId: String): LocationsError \/ Evse

  def connector(cc: String, opId: String, locId: String, evseId: String, connectorId: String): LocationsError \/ Connector

}
