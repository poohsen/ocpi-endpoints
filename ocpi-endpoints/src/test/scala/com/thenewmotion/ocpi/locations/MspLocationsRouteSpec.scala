package com.thenewmotion.ocpi.locations


import com.thenewmotion.ocpi.ApiUser
import com.thenewmotion.ocpi.locations.LocationsError._
import org.joda.time.DateTime
import org.mockito.Matchers
import Matchers.{eq => eq_}
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
        there was one(mspLocService).updateLocation(eq_(CpoId("NL", "TNM")), eq_("LOC1"), any)
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
        there was one(mspLocService).updateEvse(eq_(CpoId("NL", "TNM")), eq_("LOC1"), eq_("NL-TNM-02000000"), any)
      }
    }

    "accept patches to a connector object" in new LocationsTestScope {

      val body = HttpEntity(contentType = ContentType(`application/json`, HttpCharsets.`UTF-8`), string =
        s"""
           |{
           |    "id": "1",
           |    "status": "CHARGING"
           |}
           |""".stripMargin)

      Patch("/NL/TNM/LOC1/NL-TNM-02000000/1", body) ~> locationsRoute.route(apiUser) ~> check {
        there was one(mspLocService).updateConnector(eq_(CpoId("NL", "TNM")), eq_("LOC1"), eq_("NL-TNM-02000000"), eq_("1"), any)
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
    
    mspLocService.updateLocation(eq_(CpoId("NL", "TNM")), eq_("LOC1"), any) returns \/-(Unit)
    mspLocService.location(eq_(CpoId("NL", "TNM")), eq_("LOC1")) returns -\/(LocationsRetrievalFailed())
    mspLocService.updateEvse(eq_(CpoId("NL", "TNM")), eq_("LOC1"), eq_("NL-TNM-02000000"), any) returns \/-(Unit)
    mspLocService.updateConnector(eq_(CpoId("NL", "TNM")), eq_("LOC1"), eq_("NL-TNM-02000000"), eq_("1"),any) returns \/-(Unit)

    val apiUser = ApiUser("1", "123", "NL", "TNM")

    val locationsRoute = new MspLocationsRoute(mspLocService, authorizeAccess, dateTime1)

  }
}
