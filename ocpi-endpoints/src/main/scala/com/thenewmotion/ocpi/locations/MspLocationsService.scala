package com.thenewmotion.ocpi.locations

import com.thenewmotion.ocpi.locations.errors.LocationsError
import com.thenewmotion.ocpi.msgs.v2_0.Locations.{EvsePatch, LocationPatch}
import scalaz._

class MspLocationsService {

  def updateLocation(locPatch: LocationPatch): Unit \/ LocationsError = ???

  def updateEvse(evsePatch: EvsePatch): Unit \/ LocationsError = ???
}
