package com.thenewmotion.ocpi.locations

import com.thenewmotion.ocpi.locations.LocationsError.LocationsRetrievalFailed
import com.thenewmotion.ocpi.msgs.v2_0.CommonTypes.ErrorResp
import com.thenewmotion.ocpi.msgs.v2_0.OcpiStatusCodes.{GenericClientFailure, MissingHeader}
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

    case (LocationsErrorRejection(e@LocationsRetrievalFailed(reason))) :: _ =>
      complete {
        HttpResponse(
          NotFound,
          HttpEntity(ContentTypes.`application/json`,
            ErrorResp(
              GenericClientFailure.code,
              Some(reason),
              DateTime.now()).toJson.compactPrint)
        )
      }

  }
}
