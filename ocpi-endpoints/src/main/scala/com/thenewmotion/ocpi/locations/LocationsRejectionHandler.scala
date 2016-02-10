package com.thenewmotion.ocpi.locations

import com.thenewmotion.ocpi.locations.LocationsError._
import com.thenewmotion.ocpi.msgs.v2_0.CommonTypes.ErrorResp
import com.thenewmotion.ocpi.msgs.v2_0.OcpiStatusCodes.{GenericClientFailure}
import org.joda.time.DateTime
import spray.http.{ContentTypes, HttpEntity, HttpResponse}
import spray.http.StatusCodes._
import spray.httpx.SprayJsonSupport
import spray.json._
import spray.routing._
import spray.routing.directives.BasicDirectives
import spray.routing.directives.RouteDirectives._

object LocationsRejectionHandler extends BasicDirectives with SprayJsonSupport {

  import com.thenewmotion.ocpi.msgs.v2_0.OcpiJsonProtocol._

  val Default = RejectionHandler {

    case (LocationsErrorRejection(e@LocationCreationFailed(reason))) :: _ => complete {
        ( BadRequest,
            ErrorResp(
              GenericClientFailure.code,
              reason,
              DateTime.now()).toJson.compactPrint)
      }

    case (LocationsErrorRejection(e@LocationRetrievalFailed(reason))) :: _ => complete {
        ( NotFound,
            ErrorResp(
              GenericClientFailure.code,
              reason,
              DateTime.now()).toJson.compactPrint)
      }

    case (LocationsErrorRejection(e@EvseRetrievalFailed(reason))) :: _ => complete {
        ( NotFound,
            ErrorResp(
              GenericClientFailure.code,
              reason,
              DateTime.now()).toJson.compactPrint)
      }

    case (LocationsErrorRejection(e@ConnectorRetrievalFailed(reason))) :: _ => complete {
        ( NotFound,
            ErrorResp(
              GenericClientFailure.code,
              reason,
              DateTime.now()).toJson.compactPrint)
      }
  }
}
