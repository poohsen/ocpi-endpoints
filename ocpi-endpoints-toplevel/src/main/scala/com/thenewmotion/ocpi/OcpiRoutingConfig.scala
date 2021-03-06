package com.thenewmotion.ocpi

import com.thenewmotion.ocpi.handshake.HandshakeService
import com.thenewmotion.ocpi.msgs.v2_1.Versions.EndpointIdentifier

case class OcpiVersionConfig(
  endPoints: Map[EndpointIdentifier, Either[URI, GuardedRoute]]
)

case class OcpiRoutingConfig(
  namespace: String,
  versions: Map[String, OcpiVersionConfig],
  handshakeService: HandshakeService
)(val authenticateApiUser: String => Option[ApiUser]
)(val authenticateInternalUser: String => Option[ApiUser])
