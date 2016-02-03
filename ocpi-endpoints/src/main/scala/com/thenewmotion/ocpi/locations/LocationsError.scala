package com.thenewmotion.ocpi.locations

sealed trait LocationsError {def reason: String}

object LocationsError{

  case class LocationsRetrievalFailed(reason: String = "") extends LocationsError

  case class LocationCreationFailed(reason: String = "") extends LocationsError

}