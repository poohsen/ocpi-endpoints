package com.thenewmotion.ocpi.locations

import com.thenewmotion.ocpi.JsonApi
import com.thenewmotion.ocpi.msgs.v2_0.Locations.{EvsePatch, LocationPatch, Connector}
import org.joda.time.DateTime

class MspLocationsRoute(service: MspLocationsService, authorizeAccess: (String, String) => Boolean, currentTime: => DateTime = DateTime.now) extends JsonApi {

  import com.thenewmotion.ocpi.msgs.v2_0.OcpiJsonProtocol._

  def route = {
    patch {
      pathPrefix ( Segment / Segment / Segment ) { (cc, opId, locId) =>
        authorize(authorizeAccess(cc, opId)) {
          pathEnd {
            entity(as[LocationPatch]) { location =>
              println(location)
              complete(location)
            }
          } ~ pathPrefix ( Segment ) { evseId =>
            pathEnd {
              entity(as[EvsePatch]) { evse =>
                complete(evse)
              }
            } ~ path (Segment) { connId =>
              entity(as[Connector]) { conn =>
                complete(conn)
              }
            }
          }
        }
      }
    }
  }
}
