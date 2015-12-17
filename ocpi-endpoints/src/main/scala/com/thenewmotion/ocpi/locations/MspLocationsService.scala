package com.thenewmotion.ocpi.locations

import com.thenewmotion.ocpi.locations.Errors.LocationsError
import com.thenewmotion.ocpi.msgs.v2_0.Locations.{EvsePatch, LocationPatch}
import scalaz._

trait MspLocationsService {

  def updateLocation(cc: String, opId: String, locPatch: LocationPatch): LocationsError \/ Unit

  def updateEvse(cc: String, opId: String, evsePatch: EvsePatch): LocationsError \/ Unit
}
