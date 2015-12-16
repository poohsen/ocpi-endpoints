package com.thenewmotion.ocpi.locations

object errors {
  sealed trait LocationsError
  case object UpdateFailed extends LocationsError
}
