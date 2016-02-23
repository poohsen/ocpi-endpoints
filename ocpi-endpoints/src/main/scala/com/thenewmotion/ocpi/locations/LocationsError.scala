package com.thenewmotion.ocpi.locations

sealed trait LocationsError {def reason: Option[String]}

object LocationsError{

  case class LocationRetrievalFailed(reason: Option[String] = None) extends LocationsError
  case class LocationCreationFailed(reason: Option[String] = None) extends LocationsError
  case class LocationUpdateFailed(reason: Option[String] = None) extends LocationsError
  case class EvseRetrievalFailed(reason: Option[String] = None) extends LocationsError
  case class EvseCreationFailed(reason: Option[String] = None) extends LocationsError
  case class EvseUpdateFailed(reason: Option[String] = None) extends LocationsError
  case class ConnectorRetrievalFailed(reason: Option[String] = None) extends LocationsError
  case class ConnectorCreationFailed(reason: Option[String] = None) extends LocationsError
  case class ConnectorUpdateFailed(reason: Option[String] = None) extends LocationsError

}