package com.thenewmotion.ocpi.locations

import com.thenewmotion.ocpi.ApiUser
import com.thenewmotion.ocpi.msgs.v2_0.Locations.LocationPatch
import org.joda.time.DateTime
import org.mockito.Matchers
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope
import spray.http.MediaTypes._
import spray.http.{ContentType, HttpCharsets, HttpEntity}
import spray.testkit.Specs2RouteTest
import scalaz._

class MspLocationsRouteSpec extends Specification with Specs2RouteTest with Mockito {

  "locations endpoint" should {
    "accept patches to a location object" in new LocationsTestScope {

      val body = HttpEntity(contentType = ContentType(`application/json`, HttpCharsets.`UTF-8`), string =
        s"""
           |{
           |    "id": "LOC1",
           |    "address": "Otherstreet 12"
           |}
           |""".stripMargin)

      Patch("/NL/TNM/LOC1", body) ~> locationsRoute.route(apiUser) ~> check {
        there was one(mspLocService).updateLocation(Matchers.eq("NL"), Matchers.eq("TNM"), any)
      }
    }

    "accept patches to an EVSE object" in new LocationsTestScope {

      val body = HttpEntity(contentType = ContentType(`application/json`, HttpCharsets.`UTF-8`), string =
        s"""
           |{
           |    "id": "NL-TNM-02000000",
           |    "status": "CHARGING"
           |}
           |""".stripMargin)

      Patch("/NL/TNM/LOC1/NL-TNM-02000000", body) ~> locationsRoute.route(apiUser) ~> check {
        there was one(mspLocService).updateEvse(Matchers.eq("NL"), Matchers.eq("TNM"), any)
      }
    }

    "disallow unauthorized access" in new LocationsTestScope {
      val body = HttpEntity(contentType = ContentType(`application/json`, HttpCharsets.`UTF-8`), string = "{}")

      Patch("/BE/TNM/LOC1", body) ~> locationsRoute.route(apiUser) ~> check {
        handled must beFalse
      }
    }
  }

  trait LocationsTestScope extends Scope {

    val dateTime1 = DateTime.parse("2010-01-01T00:00:00Z")

    def authorizeAccess(cc: String, opId: String, locId: String) =
      (cc, opId, locId) match {
        case ("NL", "TNM", "LOC1") => true
        case _ => false
      }
    val mspLocService = mock[MspLocationsService]
    mspLocService.updateLocation(Matchers.eq("NL"), Matchers.eq("TNM"), any) returns \/-(Unit)
    mspLocService.updateEvse(Matchers.eq("NL"), Matchers.eq("TNM"), any) returns \/-(Unit)

    val apiUser = ApiUser("1", "123", "NL", "TNM")

    val locationsRoute = new MspLocationsRoute(mspLocService, authorizeAccess, dateTime1)

  }
}
