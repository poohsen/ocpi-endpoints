package com.thenewmotion.ocpi.msgs.v2_1

import com.thenewmotion.ocpi.msgs.v2_1.Sessions.{Session, SessionId, SessionPatch, SessionStatus}
import spray.json.{JsString, JsValue, JsonFormat, deserializationError}
import DefaultJsonProtocol._
import TokensJsonProtocol._
import CdrsJsonProtocol._
import LocationsJsonProtocol._
import com.thenewmotion.ocpi.msgs.SimpleStringEnumSerializer

trait SessionJsonProtocol {

  implicit def idFmt[T : JsonFormat]: JsonFormat[Id[T]] = new JsonFormat[Id[T]] {
    override def read(json: JsValue) = implicitly[JsonFormat[T]].read(json)
    override def write(obj: Id[T]) = implicitly[JsonFormat[T]].write(obj)
  }

  implicit val sessionIdFmt = new JsonFormat[SessionId] {
    override def read(json: JsValue) = json match {
      case JsString(s) => SessionId(s)
      case _ => deserializationError("SessionId must be a string")
    }
    override def write(obj: SessionId) = JsString(obj.value)
  }

  implicit val sessionStatusFormat =
    new SimpleStringEnumSerializer[SessionStatus](SessionStatus).enumFormat

  implicit val sessionFmt = jsonFormat13(Session.apply)

  // Oh, spray-json!  Cannot use jsonFormat13 as it fails to generate ClassManifest
  implicit val sessionPatchFmt = jsonFormat(
    SessionPatch.apply,
    "id",
    "start_datetime",
    "end_datetime",
    "kwh",
    "auth_id",
    "auth_method",
    "location",
    "meter_id",
    "currency",
    "charging_periods",
    "total_cost",
    "status",
    "last_updated"
  )
}

object SessionJsonProtocol extends SessionJsonProtocol
