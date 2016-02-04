package com.thenewmotion.ocpi.locations

import com.thenewmotion.ocpi.{ApiUser, JsonApi}
import com.thenewmotion.ocpi.msgs.v2_0.CommonTypes.SuccessResp
import com.thenewmotion.ocpi.msgs.v2_0.Locations._
import org.joda.time.DateTime
import spray.routing.{Route, Rejection}
import scalaz._

case class LocationsErrorRejection(error: LocationsError) extends Rejection

class MspLocationsRoute(
  service: MspLocationsService,
  authorizeResourceAccess: (String, String, String) => Boolean,
  currentTime: => DateTime = DateTime.now
) extends JsonApi {


  import com.thenewmotion.ocpi.msgs.v2_0.OcpiJsonProtocol._
  import com.thenewmotion.ocpi.msgs.v2_0.OcpiStatusCodes.GenericSuccess

  private def leftToRejection[T](errOrX: LocationsError \/ T)(f: T => Route): Route =
    errOrX match {
      case -\/(err) => reject(LocationsErrorRejection(err))
      case \/-(res) => f(res)
    }

  def route(apiUser: ApiUser) = {
    pathPrefix(Segment / Segment / Segment) { (cc, pId, locId) =>
      authorize(apiUser.country_code == cc && apiUser.party_id == pId &&
        authorizeResourceAccess(cc, pId, locId)) {
        pathEnd {
          patch {
            entity(as[LocationPatch]) { location =>
              leftToRejection(service.updateLocation(CpoId(cc, pId), locId, location))
                { _ => complete(SuccessResp(GenericSuccess.code, DateTime.now())) }
            }
          } ~
            put {
              entity(as[Location]) { location =>
                leftToRejection(service.createLocation(CpoId(cc, pId), locId, location))
                { _ => complete(SuccessResp(GenericSuccess.code, DateTime.now())) }
              }
            } ~
            get {
              dynamic {
                leftToRejection(service.location(CpoId(cc, pId), locId))
                { location => complete(LocationResp(GenericSuccess.code, None, DateTime.now(), location)) }
              }
            }
        } ~
          pathPrefix(Segment) { evseId =>
            pathEnd {
              patch {
                entity(as[EvsePatch]) { evse =>
                  leftToRejection(service.updateEvse(CpoId(cc, pId), locId, evseId, evse))
                    { _ => complete(SuccessResp(GenericSuccess.code, DateTime.now())) }
                }
              } ~
                put {
                  entity(as[Evse]) { evse =>
                    leftToRejection(service.addEvse(CpoId(cc, pId), locId, evseId, evse))
                      { _ => complete(SuccessResp(GenericSuccess.code, DateTime.now())) }
                  }
                } ~
                get {
                  dynamic {
                    leftToRejection(service.evse(CpoId(cc, pId), locId, evseId))
                    { evse => complete(EvseResp(GenericSuccess.code, None, DateTime.now(), evse)) }
                  }
                }
            } ~
              path(Segment) { connId =>
                patch {
                  entity(as[ConnectorPatch]) { conn =>
                    leftToRejection(service.updateConnector(CpoId(cc, pId), locId, evseId, connId, conn))
                      { _ => complete(SuccessResp(GenericSuccess.code, DateTime.now())) }
                  }
                } ~
                  put {
                    entity(as[Connector]) { conn =>
                      leftToRejection(service.addConnector(CpoId(cc, pId), locId, evseId, connId, conn))
                        { _ => complete(SuccessResp(GenericSuccess.code, DateTime.now())) }
                    }
                  } ~
                  get {
                    dynamic {
                      leftToRejection(service.connector(CpoId(cc, pId), locId, evseId, connId))
                      { connector => complete(ConnectorResp(GenericSuccess.code, None, DateTime.now(), connector)) }
                    }
                  }
              }
          }
      }
    }
  }
}

