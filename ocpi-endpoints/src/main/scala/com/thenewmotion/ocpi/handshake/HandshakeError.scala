package com.thenewmotion.ocpi.handshake

sealed trait HandshakeError {
  val reason: String
}

object HandshakeError{
  case object VersionsRetrievalFailed extends HandshakeError{
    override val reason: String = "Failed versions retrieval."
  }
  case object VersionDetailsRetrievalFailed extends HandshakeError{
    override val reason: String = "Failed version details retrieval."
  }
  case object SendingCredentialsFailed extends HandshakeError{
    override val reason: String = "Failed sending the credentials to connect to us."
  }
  case object SelectedVersionNotHostedByUs extends HandshakeError{
    override val reason: String = "The selected version is not supported by our systems."
  }
  case object CouldNotFindMutualVersion extends HandshakeError{
    override val reason: String = "Could not find mutual version."
  }
  case object SelectedVersionNotHostedByThem extends HandshakeError{
    override val reason: String = "Selected version not supported by the requester party systems."
  }
//  case object NoCredentialsEndpoint extends HandshakeError{
//    override val reason: String = "Credentials endpoint details required but not found."   }
  case object UnknownEndpointType extends HandshakeError{
    override val reason: String = s"Unknown endpoint type."
}
  case object CouldNotPersistCredsForUs extends HandshakeError{
    override val reason: String = "Could not persist credentials sent to us."
  }
  case object CouldNotPersistNewCredsForUs extends HandshakeError{
    override val reason: String = "Could not persist the new credentials sent to us."
  }
  case object CouldNotPersistNewToken extends HandshakeError{
    override val reason: String = "Could not persist the new token."
  }
  case object CouldNotPersistNewEndpoint extends HandshakeError{
    override val reason: String = "Could not persist new endpoint."
  }
  case object CouldNotUpdateEndpoints extends HandshakeError{
    override val reason: String = "Could not update registered endpoints."
  }
  case object CouldNotPersistNewParty extends HandshakeError{
    override val reason: String = "Could not persist new party."
  }
  case object AlreadyExistingParty extends HandshakeError{
    override val reason: String = "Already existing partyId for this country and version."
  }
  case object UnknownPartyToken extends HandshakeError{
    override val reason: String = "Unknown party token."
  }
  case object WaitingForRegistrationRequest extends HandshakeError{
    override val reason: String = "Still waiting for registration request."
  }
}