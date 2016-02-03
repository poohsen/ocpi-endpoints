package com.thenewmotion.ocpi.locations

object Errors{
  sealed trait LocationsError {def reason: String}

  case class LocationsRetrievalFailed(reason: String = "") extends LocationsError

  case class LocationCreationFailed(reason: String = "") extends LocationsError

}