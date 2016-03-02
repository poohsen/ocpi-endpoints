package com.thenewmotion.ocpi.handshake

import com.thenewmotion.ocpi.handshake.HandshakeError._
import com.thenewmotion.ocpi.msgs.v2_0.CommonTypes.ErrorResp
import com.thenewmotion.ocpi.msgs.v2_0.OcpiStatusCodes
import com.thenewmotion.ocpi.msgs.v2_0.OcpiStatusCodes._
import org.joda.time.DateTime
import spray.http.StatusCodes._
import spray.httpx.SprayJsonSupport
import spray.json._
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
          Some(VersionsRetrievalFailed.reason),
          DateTime.now()).toJson.compactPrint)
    }

    case HandshakeErrorRejection(VersionDetailsRetrievalFailed) :: _ => complete {
      ( FailedDependency,
        ErrorResp(
          UnableToUseApi.code,
          Some(VersionDetailsRetrievalFailed.reason),
          DateTime.now()).toJson.compactPrint)
    }

    // Initiate handshake specific error
    case HandshakeErrorRejection(SendingCredentialsFailed) :: _ => complete {
      ( BadRequest,
        ErrorResp(
          UnableToUseApi.code,
          Some(SendingCredentialsFailed.reason),
          DateTime.now()).toJson.compactPrint)
    }

    // UnsupportedVersion
    case HandshakeErrorRejection(SelectedVersionNotHostedByUs) :: _ => complete {
      ( BadRequest,
        ErrorResp(
          UnsupportedVersion.code,
          Some(SelectedVersionNotHostedByUs.reason),
          DateTime.now()).toJson.compactPrint)
    }

    case HandshakeErrorRejection(CouldNotFindMutualVersion) :: _ => complete {
      ( BadRequest,
        ErrorResp(
          UnsupportedVersion.code,
          Some(CouldNotFindMutualVersion.reason),
          DateTime.now()).toJson.compactPrint)
    }

    case HandshakeErrorRejection(SelectedVersionNotHostedByThem) :: _ => complete {
      ( BadRequest,
        ErrorResp(
          UnsupportedVersion.code,
          Some(SelectedVersionNotHostedByThem.reason),
          DateTime.now()).toJson.compactPrint)
    }

    // Endpoints
    //TODO: TNM-2013: It doesn't work yet, it must be used to fail with that error when required endpoints not included
    case (r@ValidationRejection(msg, cause)) :: _ => complete {
      ( BadRequest,
        ErrorResp(
          MissingExpectedEndpoints.code,
          Some(s"${MissingExpectedEndpoints.default_message} $msg"),
          DateTime.now()).toJson.compactPrint)
    }

    // Is recognized by OCPI msgs but not internally by the application that uses it
    case HandshakeErrorRejection(HandshakeError.UnknownEndpointType) :: _ => complete {
      ( InternalServerError,
        ErrorResp(
          OcpiStatusCodes.UnknownEndpointType.code,
          Some(HandshakeError.UnknownEndpointType.reason),
          DateTime.now()).toJson.compactPrint)
    }

    // GenericServerFailure
    case HandshakeErrorRejection(CouldNotPersistCredsForUs) :: _ => complete {
      ( InternalServerError,
        ErrorResp(
          GenericServerFailure.code,
          Some(CouldNotPersistCredsForUs.reason),
          DateTime.now()).toJson.compactPrint)
    }

    case HandshakeErrorRejection(CouldNotPersistNewCredsForUs) :: _ => complete {
      ( InternalServerError,
        ErrorResp(
          GenericServerFailure.code,
          Some(CouldNotPersistNewCredsForUs.reason),
          DateTime.now()).toJson.compactPrint)
    }

    case HandshakeErrorRejection(CouldNotPersistNewToken) :: _ => complete {
      ( InternalServerError,
        ErrorResp(
          GenericServerFailure.code,
          Some(CouldNotPersistNewToken.reason),
          DateTime.now()).toJson.compactPrint)
    }

    case HandshakeErrorRejection(CouldNotPersistNewEndpoint) :: _ => complete {
      ( InternalServerError,
        ErrorResp(
          GenericServerFailure.code,
          Some(CouldNotPersistNewEndpoint.reason),
          DateTime.now()).toJson.compactPrint)
    }

    case HandshakeErrorRejection(CouldNotUpdateEndpoints) :: _ => complete {
      ( InternalServerError,
        ErrorResp(
          GenericServerFailure.code,
          Some(CouldNotUpdateEndpoints.reason),
          DateTime.now()).toJson.compactPrint)
    }

    case HandshakeErrorRejection(CouldNotPersistNewParty) :: _ => complete {
      ( InternalServerError,
        ErrorResp(
          GenericServerFailure.code,
          Some(CouldNotPersistNewParty.reason),
          DateTime.now()).toJson.compactPrint)
    }

    // Not allowed
    case HandshakeErrorRejection(AlreadyExistingParty) :: _ => complete {
      ( Conflict,
        ErrorResp(
          PartyAlreadyRegistered.code,
          Some(AlreadyExistingParty.reason),
          DateTime.now()).toJson.compactPrint)
    }

    case HandshakeErrorRejection(UnknownPartyToken) :: _ => complete {
      ( BadRequest,
        ErrorResp(
          AuthenticationFailed.code,
          Some(UnknownPartyToken.reason),
          DateTime.now()).toJson.compactPrint)
    }

    case HandshakeErrorRejection(WaitingForRegistrationRequest) :: _ => complete {
      ( BadRequest,
        ErrorResp(
          RegistrationNotCompletedYetByParty.code,
          Some(WaitingForRegistrationRequest.reason),
          DateTime.now()).toJson.compactPrint)
    }

  }
}
