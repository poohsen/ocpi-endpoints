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
    case HandshakeErrorRejection(e@SelectedVersionNotHostedByUs(v)) :: _ => complete {
      ( BadRequest,
        ErrorResp(
          UnsupportedVersion.code,
          e.reason))
    }

    case HandshakeErrorRejection(CouldNotFindMutualVersion) :: _ => complete {
      ( BadRequest,
        ErrorResp(
          UnsupportedVersion.code,
          CouldNotFindMutualVersion.reason))
    }

    case HandshakeErrorRejection(e@SelectedVersionNotHostedByThem(v)) :: _ => complete {
      ( BadRequest,
        ErrorResp(
          UnsupportedVersion.code,
          e.reason))
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
    case HandshakeErrorRejection(e@HandshakeError.UnknownEndpointType(ep)) :: _ => complete {
      ( InternalServerError,
        ErrorResp(
          OcpiStatusCodes.UnknownEndpointType.code,
          e.reason))
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

    case HandshakeErrorRejection(e@CouldNotPersistNewToken(t)) :: _ => complete {
      ( InternalServerError,
        ErrorResp(
          GenericServerFailure.code,
          e.reason))

    }

    case HandshakeErrorRejection(e@CouldNotPersistNewEndpoint(ep)) :: _ => complete {
      ( InternalServerError,
        ErrorResp(
          GenericServerFailure.code,
          e.reason))

    }

    case HandshakeErrorRejection(CouldNotUpdateEndpoints) :: _ => complete {
      ( InternalServerError,
        ErrorResp(
          GenericServerFailure.code,
          CouldNotUpdateEndpoints.reason))

    }

    case HandshakeErrorRejection(e@CouldNotPersistNewParty(p)) :: _ => complete {
      ( InternalServerError,
        ErrorResp(
          GenericServerFailure.code,
          e.reason))

    }

    // Not allowed
    case HandshakeErrorRejection(e@AlreadyExistingParty(p, c, v)) :: _ => complete {
      ( Conflict,
        ErrorResp(
          PartyAlreadyRegistered.code,
          e.reason))

    }

    case HandshakeErrorRejection(e@UnknownPartyToken(t)) :: _ => complete {
      ( BadRequest,
        ErrorResp(
          AuthenticationFailed.code,
          e.reason))

    }

    case HandshakeErrorRejection(WaitingForRegistrationRequest) :: _ => complete {
      ( BadRequest,
        ErrorResp(
          RegistrationNotCompletedYetByParty.code,
          WaitingForRegistrationRequest.reason))

    }

  }


  import spray.httpx.marshalling._
  import spray.http._

  implicit val handshakeErrorToResponseMarshaller: ToResponseMarshaller[HandshakeError] =
    implicitly[ToResponseMarshaller[(StatusCode, ErrorResp)]]
    .compose[HandshakeError] { e =>
      val (status, cec) = e match {
        case VersionsRetrievalFailed => (FailedDependency, UnableToUseApi)
        case VersionDetailsRetrievalFailed => (FailedDependency, UnableToUseApi)
        case SendingCredentialsFailed => (BadRequest, UnableToUseApi)
        case SelectedVersionNotHostedByUs(v) => (BadRequest, UnsupportedVersion)
        case CouldNotFindMutualVersion => (BadRequest, UnsupportedVersion)
        case SelectedVersionNotHostedByThem(_) => (BadRequest, UnsupportedVersion)
        // case (r@ValidationRejection(msg, cause)=>
        //   (BadRequest, (MissingExpectedEndpoints.code,
        //       s"${MissingExpectedEndpoints.default_message} $msg"))
        case HandshakeError.UnknownEndpointType(_) => (InternalServerError, OcpiStatusCodes.UnknownEndpointType)
        case CouldNotPersistCredsForUs => (InternalServerError, GenericServerFailure)
        case CouldNotPersistNewCredsForUs => (InternalServerError, GenericServerFailure)
        case CouldNotPersistNewToken(_) => (InternalServerError, GenericServerFailure)
        case CouldNotPersistNewEndpoint(_) => (InternalServerError, GenericServerFailure)
        case CouldNotUpdateEndpoints => (InternalServerError, GenericServerFailure)
        case CouldNotPersistNewParty(p) => (InternalServerError, GenericServerFailure)
        case AlreadyExistingParty(p, c, v) => (Conflict, PartyAlreadyRegistered)
        case UnknownPartyToken(t) => (BadRequest, AuthenticationFailed)
        case WaitingForRegistrationRequest => (BadRequest, RegistrationNotCompletedYetByParty)
      }

      (status, ErrorResp(cec.code, e.reason))
    }
}
