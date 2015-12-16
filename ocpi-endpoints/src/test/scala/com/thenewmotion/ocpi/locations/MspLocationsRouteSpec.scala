package com.thenewmotion.ocpi.locations

import org.joda.time.DateTime
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope
import spray.http.MediaTypes._
import spray.http.{ContentType, HttpCharsets, HttpEntity}
import spray.testkit.Specs2RouteTest

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

      Patch("/NL/TNM/LOC1", body) ~> locationsRoute.route ~> check {
        handled must beTrue
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

      Patch("/NL/TNM/LOC1/NL-TNM-02000000", body) ~> locationsRoute.route ~> check {
        handled must beTrue
      }
    }

    "disallow unauthorized access" in new LocationsTestScope {
      val body = HttpEntity(contentType = ContentType(`application/json`, HttpCharsets.`UTF-8`), string = "{}")

      Patch("/BE/TNM/LOC1", body) ~> locationsRoute.route ~> check {
        handled must beFalse
      }
    }
  }

  trait LocationsTestScope extends Scope {

    val dateTime1 = DateTime.parse("2010-01-01T00:00:00Z")

    def authorizeAccess(cc: String, opId: String) =
      (cc, opId) match {
        case ("NL", "TNM") => true
        case _ => false
      }

    val locationsRoute = new MspLocationsRoute(new MspLocationsService, authorizeAccess, dateTime1)

  }
}
