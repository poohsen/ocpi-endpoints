package com.thenewmotion.ocpi.msgs.v2_1

import Locations.PowerType.AC3Phase
import Locations._
import CommonTypes._
import org.joda.time.format.ISODateTimeFormat
import org.specs2.mutable.SpecificationWithJUnit
import org.specs2.specification.Scope
import spray.json._

class LocationsSpecs extends SpecificationWithJUnit {

  import OcpiJsonProtocol._

  "Evses" should {
    "deserialize" in new LocationsTestScope {
      evseJson1.convertTo[Evse] mustEqual evse1
    }
    "serialize" in new LocationsTestScope {
      evse1.toJson.toString mustEqual evseJson1.compactPrint
    }
  }

   "LocationResp" should {
      "deserialize" in new LocationsTestScope {
         locationRespJson1.convertTo[SuccessWithDataResp[List[Location]]] mustEqual locationResp1
      }
      "serialize" in new LocationsTestScope {
         locationResp1.toJson.toString mustEqual locationRespJson1.compactPrint
      }
   }

  "Operator" should {
    "deserialize" in new LocationsTestScope {
      operatorJsonWithNulls1.convertTo[Operator] mustEqual operator1
    }
    "serialize" in new LocationsTestScope {
      operator1.toJson.toString mustEqual operatorJsonNoNulls1.compactPrint
    }
  }

  "Power" should {
    "deserialize" in new LocationsTestScope {
      powerJson1.convertTo[Power] mustEqual power1
    }
    "serialize" in new LocationsTestScope {
      power1.toJson.toString mustEqual powerJson1.compactPrint
    }
  }

  "PeriodTypeJsonFormat" should {
    val periodType: PeriodType = PeriodType.Charging
    val str = "Charging"
    "serialize" in {
      periodType.toJson mustEqual JsString(str)
    }
    "extract" in {
      JsonParser("\"" + str + "\"").convertTo[PeriodType] mustEqual periodType
    }
  }


  private trait LocationsTestScope extends Scope {

    val formatter = ISODateTimeFormat.dateTimeNoMillis().withZoneUTC
    val date1 = formatter.parseDateTime("2010-01-01T00:00:00Z")
    val date2 = formatter.parseDateTime("2020-12-31T23:59:59Z")
    val dateOfUpdate = formatter.parseDateTime("2016-12-31T23:59:59Z")


    val displayText_standard = List(DisplayText("nl", "Standaard Tarief"),
      DisplayText("en", "Standard Tariff") )
    val displayText_emsp =  List(DisplayText("nl", "eMSP Tarief"),
      DisplayText("en", "eMSP Tariff") )



     val connector1 = Connector(
        "1",
        lastUpdated = dateOfUpdate,
        ConnectorType.`IEC_62196_T2`,
        ConnectorFormat.Cable,
        PowerType.AC3Phase,
        230,
        16,
        tariffId = Some("kwrate")
     )

    val connector2 = Connector(
      "2",
      lastUpdated = dateOfUpdate,
      ConnectorType.`IEC_62196_T2`,
      ConnectorFormat.Socket,
      PowerType.AC3Phase,
      230,
      16,
      tariffId = Some("timerate")
    )

    val evse1 = Evse(
      "BE-BEC-E041503001",
      lastUpdated = dateOfUpdate,
      ConnectorStatus.Available,
      capabilities = List(Capability.Reservable),
      connectors = List(connector1, connector2),
      floorLevel = Some("-1"),
      physicalReference = Some("1")
    )

    val evse2 = Evse(
      "BE-BEC-E041503002",
      lastUpdated = dateOfUpdate,
      ConnectorStatus.Available,
      capabilities = List(Capability.Reservable),
      connectors = List(connector1),
      floorLevel = Some("-1"),
      physicalReference = Some("1")
    )

    val evse3 = Evse(
      "BE-BEC-E041503003",
      lastUpdated = dateOfUpdate,
      ConnectorStatus.Available,
      capabilities = List(Capability.Reservable),
      connectors = List(connector1),
      floorLevel = Some("-1"),
      physicalReference = Some("2")
    )

    val excp_open_begin = formatter.parseDateTime("2014-06-21T09:00:00+02:00")
    val excp_open_end = formatter.parseDateTime("2014-06-21T12:00:00+02:00")
    val excp_close_begin = formatter.parseDateTime("2014-06-24T00:00:00+02:00")
    val excp_close_end = formatter.parseDateTime("2014-06-25T00:00:00+02:00")

    val hours1 = Hours(
      regularHours = List(
        RegularHours(1, "08:00", "20:00"),
        RegularHours(2, "08:00", "20:00"),
        RegularHours(3, "08:00", "20:00"),
        RegularHours(4, "08:00", "20:00"),
        RegularHours(5, "08:00", "20:00")
      ),
      twentyfourseven = false,
      exceptionalOpenings = List(
        ExceptionalPeriod(excp_open_begin, excp_open_end)),
      exceptionalClosings = List(
        ExceptionalPeriod(excp_close_begin, excp_close_end)
      )
    )
    val dir1 = DisplayText("en", "left, left, left, right, left")
    val location1 = Location(
      "LOC1",
      lastUpdated = dateOfUpdate,
      `type` = LocationType.OnStreet,
      Some("Gent Zuid"),
      address = "F.Rooseveltlaan 3A",
      city = "Gent",
      postalCode = "9000",
      country = "BEL",
      coordinates = GeoLocation("3.729945", "51.047594"),
      evses = List(evse1),
      directions = List(dir1),
      operator = None,
      suboperator = None,
      openingTimes = Some(hours1),
      relatedLocations = List.empty,
      chargingWhenClosed = Some(true),
      images = List.empty
    )

    val location2 = Location(
      "LOC2",
      lastUpdated = dateOfUpdate,
      `type` = LocationType.OnStreet,
      Some("Gent Zuid"),
      address = "F.Rooseveltlaan 30",
      city = "Gent",
      postalCode = "9000",
      country = "BEL",
      coordinates = GeoLocation("3.729955", "51.047604"),
      evses = List(evse2,evse3),
      directions = List(dir1),
      operator = None,
      suboperator = None,
      openingTimes = None,
      relatedLocations = List.empty,
      chargingWhenClosed = Some(true),
      images = List.empty
    )

    val locationResp1 = SuccessWithDataResp(
      OcpiStatusCode.GenericSuccess,
      Some("OK"),
      timestamp = date1,
      data = List(location1,location2)
    )

    val power1 = Power(Some(PowerType.AC3Phase), 16, 230)

    val geoLocationJson1 =
      s"""
         |{
         |  "latitude": "3.729945",
         |  "longitude": "51.047594"
         |}
     """.stripMargin.parseJson

    val geoLocationJson2 =
      s"""
         |{
         |  "latitude": "3.729955",
         |  "longitude": "51.047604"
         |}
     """.stripMargin.parseJson



    val evseJson1 =
      s"""
         |    {
         |      "uid": "BE-BEC-E041503001",
         |      "last_updated": "2016-12-31T23:59:59Z",
         |      "status": "AVAILABLE",
         |      "status_schedule": [],
         |      "capabilities": [
         |        "RESERVABLE"
         |      ],
         |      "connectors": [
         |        {
         |          "id": "1",
         |          "last_updated": "2016-12-31T23:59:59Z",
         |          "standard": "IEC_62196_T2",
         |          "format": "CABLE",
         |          "power_type": "AC_3_PHASE",
         |          "voltage": 230,
         |          "amperage": 16,
         |          "tariff_id": "kwrate"
         |        },
         |        {
         |          "id": "2",
         |          "last_updated": "2016-12-31T23:59:59Z",
         |          "standard": "IEC_62196_T2",
         |          "format": "SOCKET",
         |          "power_type": "AC_3_PHASE",
         |          "voltage": 230,
         |          "amperage": 16,
         |          "tariff_id": "timerate"
         |        }
         |      ],
         |      "directions": [],
         |      "floor_level": "-1",
         |      "images": [],
         |      "parking_restrictions": [],
         |      "physical_reference": "1",
         |      "floor_level": "-1"
         |    }
   """.stripMargin.parseJson




    val evseJson2 =
      s"""
         |    {
         |      "uid": "BE-BEC-E041503002",
         |      "last_updated": "2016-12-31T23:59:59Z",
         |      "status": "AVAILABLE",
         |      "status_schedule": [],
         |      "capabilities": [
         |        "RESERVABLE"
         |      ],
         |      "connectors": [
         |        {
         |          "id": "1",
         |          "last_updated": "2016-12-31T23:59:59Z",
         |          "standard": "IEC_62196_T2",
         |          "format": "CABLE",
         |          "power_type": "AC_3_PHASE",
         |          "voltage": 230,
         |          "amperage": 16,
         |          "tariff_id": "kwrate"
         |        }
         |      ],
         |      "directions": [],
         |      "floor_level": "-1",
         |      "images": [],
         |      "parking_restrictions": [],
         |      "physical_reference": "1",
         |      "floor_level": "-1"
         |    }
   """.stripMargin.parseJson

    val evseJson3 =
      s"""
         |    {
         |      "uid": "BE-BEC-E041503003",
         |      "last_updated": "2016-12-31T23:59:59Z",
         |      "status": "AVAILABLE",
         |      "status_schedule": [],
         |      "capabilities": [
         |        "RESERVABLE"
         |      ],
         |      "connectors": [
         |        {
         |          "id": "1",
         |          "last_updated": "2016-12-31T23:59:59Z",
         |          "standard": "IEC_62196_T2",
         |          "format": "CABLE",
         |          "power_type": "AC_3_PHASE",
         |          "voltage": 230,
         |          "amperage": 16,
         |          "tariff_id": "kwrate"
         |        }
         |      ],
         |      "directions": [],
         |      "floor_level": "-1",
         |      "images": [],
         |      "parking_restrictions": [],
         |      "physical_reference": "2",
         |      "floor_level": "-1"
         |    }
   """.stripMargin.parseJson




    val locationJson1 =
      s"""
         |    {
         |      "id": "LOC1",
         |      "last_updated": "2016-12-31T23:59:59Z",
         |      "type": "ON_STREET",
         |      "name": "Gent Zuid",
         |      "address": "F.Rooseveltlaan 3A",
         |      "city": "Gent",
         |      "postal_code": "9000",
         |      "country": "BEL",
         |      "coordinates": $geoLocationJson1,
         |      "related_locations": [],
         |      "evses": [$evseJson1],
         |      "directions": [{"language":"en","text":"left, left, left, right, left"}],
         |      "opening_times": {
         |        "twentyfourseven": false,
         |		    "regular_hours": [
         |		      {
         |		        "weekday": 1,
         |		        "period_begin": "08:00",
         |		        "period_end": "20:00"
         |		      },
         |		      {
         |		        "weekday": 2,
         |		        "period_begin": "08:00",
         |		        "period_end": "20:00"
         |		      },
         |		      {
         |		        "weekday": 3,
         |		        "period_begin": "08:00",
         |		        "period_end": "20:00"
         |		      },
         |		      {
         |		        "weekday": 4,
         |		        "period_begin": "08:00",
         |		        "period_end": "20:00"
         |		      },
         |		      {
         |		        "weekday": 5,
         |		        "period_begin": "08:00",
         |		        "period_end": "20:00"
         |		      }
         |		    ],
         |		    "exceptional_openings": [
         |		      {
         |		        "period_begin": "2014-06-21T07:00:00Z",
         |		        "period_end": "2014-06-21T10:00:00Z"
         |		      }
         |		    ],
         |		    "exceptional_closings": [
         |		      {
         |		        "period_begin": "2014-06-23T22:00:00Z",
         |		        "period_end": "2014-06-24T22:00:00Z"
         |		      }
         |		    ]
         |		  },
         |      "facilities": [],
         |      "images":[],
         |      "charging_when_closed": true
         |    }
   """.stripMargin.parseJson

    val locationJson2 =
      s"""
         |    {
         |      "id": "LOC2",
         |      "last_updated": "2016-12-31T23:59:59Z",
         |      "type": "ON_STREET",
         |      "name": "Gent Zuid",
         |      "address": "F.Rooseveltlaan 30",
         |      "city": "Gent",
         |      "postal_code": "9000",
         |      "country": "BEL",
         |      "coordinates": $geoLocationJson2,
         |      "related_locations": [],
         |      "evses": [$evseJson2, $evseJson3],
         |      "directions": [{"language":"en","text":"left, left, left, right, left"}],
         |      "facilities": [],
         |      "images":[],
         |      "charging_when_closed": true
         |    }
   """.stripMargin.parseJson

    val locationRespJson1 =
      s"""
         |{
         |  "status_code": 1000,
         |  "status_message": "OK",
         |  "timestamp": "${date1.toString(formatter)}",
         |  "data": [$locationJson1,$locationJson2]
         |}
       """.stripMargin.parseJson

  }

  val operatorJsonWithNulls1 =
    s"""
       |{
       |  "identifier": null,
       |  "phone": "+31253621489",
       |  "url": null
       |}
    """.stripMargin.parseJson

  val operatorJsonNoNulls1 =
    s"""
       |{
       |  "phone": "+31253621489"
       |}
    """.stripMargin.parseJson

  val operator1 = Operator(None, Some("+31253621489"), None)

  val powerJson1 =
    s"""
       |{
       |  "current": "AC_3_PHASE",
       |  "amperage": 16,
       |  "voltage": 230
       |}
     """.stripMargin.parseJson

  val power1 = Power(Some(AC3Phase), 16, 230)


}
