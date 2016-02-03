package com.thenewmotion.ocpi.locations

import com.thenewmotion.ocpi.{ApiUser, JsonApi}
import com.thenewmotion.ocpi.msgs.v2_0.CommonTypes.SuccessResp
import com.thenewmotion.ocpi.msgs.v2_0.Locations._
import org.joda.time.DateTime
import scalaz._

class MspLocationsRoute(
  service: MspLocationsService,
  authorizeResourceAccess: (String, String, String) => Boolean,
  currentTime: => DateTime = DateTime.now
) extends JsonApi {

  import com.thenewmotion.ocpi.msgs.v2_0.OcpiJsonProtocol._
  import com.thenewmotion.ocpi.msgs.v2_0.OcpiStatusCodes.GenericSuccess

  def route(apiUser: ApiUser) = {
    pathPrefix(Segment / Segment / Segment) { (cc, pId, locId) =>
      authorize( apiUser.country_code == cc && apiUser.party_id == pId &&
        authorizeResourceAccess(cc, pId, locId)) {
        pathEnd {
          patch {
            entity(as[LocationPatch]) { location =>
              service.updateLocation(cc, pId, location) match {
                case -\/(err) => reject(LocationUpdateRejection(err.reason))
                case \/-(_) => complete(SuccessResp(GenericSuccess.code, DateTime.now()))
              }
            }
          } ~
          put {
            entity(as[Location]) { location =>
              service.createLocation(cc, pId, location) match {
                case -\/(err) => reject(LocationCreationRejection(err.reason))
                case \/-(_) => complete(SuccessResp(GenericSuccess.code, DateTime.now()))
              }
            }
          } ~
          get {
            service.location(cc, pId, locId) match {
              case -\/(errorMsg) => reject()
              case \/-(location) => complete(LocationResp(GenericSuccess.code, None, DateTime.now(), location))
            }
          }
        }
        pathPrefix(Segment) { evseId =>
          pathEnd {
            patch {
              entity(as[EvsePatch]) { evse =>
                service.updateEvse(cc, pId, evseId, evse) match {
                  case -\/(_) => reject()
                  case \/-(_) => complete(SuccessResp(GenericSuccess.code, DateTime.now()))
                }
              }
            } ~
              put {
                entity(as[Evse]) { evse =>
                  service.addEvse(cc, pId, locId, evseId, evse) match {
                    case -\/(_) => reject()
                    case \/-(_) => complete(SuccessResp(GenericSuccess.code, DateTime.now()))
                  }
                }
              } ~
              get {
                service.evse(cc, pId, locId, evseId) match {
                  case -\/(_) => reject()
                  case \/-(evse) => complete(EvseResp(GenericSuccess.code, None, DateTime.now(), evse))
                }
              }
          } ~
          path(Segment) { connId =>
            patch {
              entity(as[ConnectorPatch]) { conn =>
                service.updateConnector(cc, pId, evseId, connId, conn) match {
                  case -\/(_) => reject()
                  case \/-(_) => complete(SuccessResp(GenericSuccess.code, DateTime.now()))
                }
              }
            } ~
            put {
              entity(as[Connector]) { conn =>
                service.addConnector(cc, pId, locId, evseId, connId, conn) match {
                  case -\/(_) => reject()
                  case \/-(_) => complete(SuccessResp(GenericSuccess.code, DateTime.now()))
                }
              }
            } ~
            get {
              service.connector(cc, pId, locId, evseId, connId) match {
                case -\/(_) => reject()
                case \/-(connector) => complete(ConnectorResp(GenericSuccess.code, None, DateTime.now(), connector))
              }
            }
          }
        }
      }
    }
  }
}
