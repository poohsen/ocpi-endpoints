package com.thenewmotion.ocpi.locations

import com.thenewmotion.ocpi.{ApiUser, JsonApi}
import com.thenewmotion.ocpi.msgs.v2_0.CommonTypes.SuccessResp
import com.thenewmotion.ocpi.msgs.v2_0.Locations.{EvsePatch, LocationPatch, Connector}
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
    put {
      complete("not yet implemented")
    } ~
    patch {
      pathPrefix ( Segment / Segment / Segment ) { (cc, pId, locId) =>
        authorize( apiUser.country_code == cc && apiUser.party_id == pId &&
                  authorizeResourceAccess(cc, pId, locId)) {
          pathEnd {
            entity(as[LocationPatch]) { location =>
              service.updateLocation(cc, pId, location) match {
                case -\/(_) => reject()
                case \/-(_) => complete(SuccessResp(GenericSuccess.code, DateTime.now()))
              }
            }
          } ~ pathPrefix ( Segment ) { evseId =>
            pathEnd {
              entity(as[EvsePatch]) { evse =>
                service.updateEvse(cc, pId, evse) match {
                  case -\/(_) => reject()
                  case \/-(_) => complete(SuccessResp(GenericSuccess.code, DateTime.now()))
                }
              }
            } ~ path (Segment) { connId =>
              entity(as[Connector]) { conn =>
                complete(conn)
              }
            }
          }
        }
      }
    } ~
    get {
      complete("not yet implemented")
    }
  }
}
