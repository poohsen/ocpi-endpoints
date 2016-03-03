package com.thenewmotion.ocpi.handshake

import com.thenewmotion.ocpi.handshake.HandshakeError._
import com.thenewmotion.ocpi.msgs.v2_0.CommonTypes.ErrorResp
import com.thenewmotion.ocpi.msgs.v2_0.OcpiStatusCodes
import com.thenewmotion.ocpi.msgs.v2_0.OcpiStatusCodes._
import spray.http.StatusCodes._
import spray.httpx.SprayJsonSupport
import spray.routing._
import spray.routing.directives.{MiscDirectives, BasicDirectives}
import spray.routing.directives.RouteDirectives._

object HandshakeRejectionHandler  extends BasicDirectives with MiscDirectives with SprayJsonSupport {

  import com.thenewmotion.ocpi.msgs.v2_0.OcpiJsonProtocol._

  val Default = RejectionHandler {

    // UnableToUseApi
    case HandshakeErrorRejection(VersionsRetrievalFailed) :: _ => complete {
      ( FailedDependency,
        ErrorResp(
          UnableToUseApi.code,
          VersionsRetrievalFailed.reason))
    }

    case HandshakeErrorRejection(VersionDetailsRetrievalFailed) :: _ => complete {
      ( FailedDependency,
        ErrorResp(
          UnableToUseApi.code,
          VersionDetailsRetrievalFailed.reason))
    }

    // Initiate handshake specific error
    case HandshakeErrorRejection(SendingCredentialsFailed) :: _ => complete {
      ( BadRequest,
        ErrorResp(
          UnableToUseApi.code,
          SendingCredentialsFailed.reason))
    }

    // UnsupportedVersion
    case HandshakeErrorRejection(SelectedVersionNotHostedByUs) :: _ => complete {
      ( BadRequest,
        ErrorResp(
          UnsupportedVersion.code,
          SelectedVersionNotHostedByUs.reason))
    }

    case HandshakeErrorRejection(CouldNotFindMutualVersion) :: _ => complete {
      ( BadRequest,
        ErrorResp(
          UnsupportedVersion.code,
          CouldNotFindMutualVersion.reason))
    }

    case HandshakeErrorRejection(SelectedVersionNotHostedByThem) :: _ => complete {
      ( BadRequest,
        ErrorResp(
          UnsupportedVersion.code,
          SelectedVersionNotHostedByThem.reason))
    }

    // Endpoints
    //TODO: TNM-2013: It doesn't work yet, it must be used to fail with that error when required endpoints not included
    case (r@ValidationRejection(msg, cause)) :: _ => complete {
      ( BadRequest,
        ErrorResp(
          MissingExpectedEndpoints.code,
          s"${MissingExpectedEndpoints.default_message} $msg"))
    }

    // Is recognized by OCPI msgs but not internally by the application that uses it
    case HandshakeErrorRejection(HandshakeError.UnknownEndpointType) :: _ => complete {
      ( InternalServerError,
        ErrorResp(
          OcpiStatusCodes.UnknownEndpointType.code,
          HandshakeError.UnknownEndpointType.reason))
    }

    // GenericServerFailure
    case HandshakeErrorRejection(CouldNotPersistCredsForUs) :: _ => complete {
      ( InternalServerError,
        ErrorResp(
          GenericServerFailure.code,
          CouldNotPersistCredsForUs.reason))
    }

    case HandshakeErrorRejection(CouldNotPersistNewCredsForUs) :: _ => complete {
      ( InternalServerError,
        ErrorResp(
          GenericServerFailure.code,
          CouldNotPersistNewCredsForUs.reason))
          
    }

    case HandshakeErrorRejection(CouldNotPersistNewToken) :: _ => complete {
      ( InternalServerError,
        ErrorResp(
          GenericServerFailure.code,
          CouldNotPersistNewToken.reason))
          
    }

    case HandshakeErrorRejection(CouldNotPersistNewEndpoint) :: _ => complete {
      ( InternalServerError,
        ErrorResp(
          GenericServerFailure.code,
          CouldNotPersistNewEndpoint.reason))
          
    }

    case HandshakeErrorRejection(CouldNotUpdateEndpoints) :: _ => complete {
      ( InternalServerError,
        ErrorResp(
          GenericServerFailure.code,
          CouldNotUpdateEndpoints.reason))
          
    }

    case HandshakeErrorRejection(CouldNotPersistNewParty) :: _ => complete {
      ( InternalServerError,
        ErrorResp(
          GenericServerFailure.code,
          CouldNotPersistNewParty.reason))
          
    }

    // Not allowed
    case HandshakeErrorRejection(AlreadyExistingParty) :: _ => complete {
      ( Conflict,
        ErrorResp(
          PartyAlreadyRegistered.code,
          AlreadyExistingParty.reason))
          
    }

    case HandshakeErrorRejection(UnknownPartyToken) :: _ => complete {
      ( BadRequest,
        ErrorResp(
          AuthenticationFailed.code,
          UnknownPartyToken.reason))
          
    }

    case HandshakeErrorRejection(WaitingForRegistrationRequest) :: _ => complete {
      ( BadRequest,
        ErrorResp(
          RegistrationNotCompletedYetByParty.code,
          WaitingForRegistrationRequest.reason))
          
    }

  }
}
