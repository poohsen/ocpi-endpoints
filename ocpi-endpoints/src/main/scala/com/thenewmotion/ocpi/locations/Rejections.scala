package com.thenewmotion.ocpi.locations

import spray.routing.Rejection

case class LocationCreationRejection(reason: String) extends Rejection
case class LocationUpdateRejection(reason: String) extends Rejection
