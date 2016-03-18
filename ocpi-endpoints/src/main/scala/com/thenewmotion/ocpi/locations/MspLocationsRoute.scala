package com.thenewmotion.ocpi.locations

import com.thenewmotion.ocpi.{ApiUser, JsonApi}
import com.thenewmotion.ocpi.msgs.v2_0.CommonTypes.SuccessResp
import com.thenewmotion.ocpi.msgs.v2_0.Locations._
import org.joda.time.DateTime
import spray.http.{StatusCodes, HttpMethods}
import spray.routing.{MethodRejection, Route, Rejection}
import scala.concurrent.{ExecutionContext, Future}
import scalaz._

case class LocationsErrorRejection(error: LocationsError) extends Rejection

class MspLocationsRoute(
  service: MspLocationsService,
  isResourceAccessAuthorized: (String, String, String) => Boolean,
  currentTime: => DateTime = DateTime.now
) (implicit ec: ExecutionContext) extends JsonApi {


  import com.thenewmotion.ocpi.msgs.v2_0.OcpiJsonProtocol._
  import com.thenewmotion.ocpi.msgs.v2_0.OcpiStatusCodes.GenericSuccess

  private def leftToRejection[T](errOrX: Future[LocationsError \/ T])(f: T => Route)(implicit ec: ExecutionContext): Route =
    onSuccess(errOrX) {
      case -\/(e) => reject(LocationsErrorRejection(e))
      case \/-(r) => f(r)
    }

  def route(apiUser: ApiUser) =
    handleRejections(LocationsRejectionHandler.Default) (routeWithoutRh(apiUser))


  private [locations] def routeWithoutRh(apiUser: ApiUser) = {
    pathPrefix(Segment / Segment / Segment) { (cc, pId, locId) =>
      pathEndOrSingleSlash {
        cancelRejection(MethodRejection(HttpMethods.PUT)){
          put {
            authorize(apiUser.countryCode == cc && apiUser.partyId == pId) {
              entity(as[Location]) { location =>
                leftToRejection(service.createOrUpdateLocation(CpoId(cc, pId), locId, location)) { res =>
                  complete((if(res)StatusCodes.Created else StatusCodes.OK, SuccessResp(GenericSuccess.code))) }
              }
            }
          }
        }
      } ~
      authorize(apiUser.countryCode == cc && apiUser.partyId == pId && isResourceAccessAuthorized(cc, pId, locId)) {
        pathEndOrSingleSlash {
          patch {
            entity(as[LocationPatch]) { location =>
              leftToRejection(service.updateLocation(CpoId(cc, pId), locId, location)){ _ =>
                complete(SuccessResp(GenericSuccess.code)) }
            }
          } ~
          get {
            dynamic {
              leftToRejection(service.location(CpoId(cc, pId), locId)) { location =>
                complete(LocationResp(GenericSuccess.code, None, data = location)) }
            }
          }
        } ~
          pathPrefix(Segment) { evseId =>
            pathEndOrSingleSlash {
              put {
                entity(as[Evse]) { evse =>
                  leftToRejection(service.addOrUpdateEvse(CpoId(cc, pId), locId, evseId, evse)) { res =>
                    complete((if(res)StatusCodes.Created else StatusCodes.OK, SuccessResp(GenericSuccess.code))) }
                }
              } ~
                patch {
                  entity(as[EvsePatch]) { evse =>
                    leftToRejection(service.updateEvse(CpoId(cc, pId), locId, evseId, evse)) { _ =>
                      complete(SuccessResp(GenericSuccess.code)) }
                  }
                } ~
                get {
                  dynamic {
                    leftToRejection(service.evse(CpoId(cc, pId), locId, evseId)) { evse =>
                      complete(EvseResp(GenericSuccess.code, None, data = evse)) }
                  }
                }
            } ~
              (path(Segment) & pathEndOrSingleSlash) { connId =>
                put {
                  entity(as[Connector]) { conn =>
                    leftToRejection(service.addOrUpdateConnector(CpoId(cc, pId), locId, evseId, connId, conn)) { res =>
                      complete((if(res)StatusCodes.Created else StatusCodes.OK, SuccessResp(GenericSuccess.code))) }
                  }
                } ~
                  patch {
                    entity(as[ConnectorPatch]) { conn =>
                      leftToRejection(service.updateConnector(CpoId(cc, pId), locId, evseId, connId, conn)) { _ =>
                        complete(SuccessResp(GenericSuccess.code)) }
                    }
                  } ~
                  get {
                    dynamic {
                      leftToRejection(service.connector(CpoId(cc, pId), locId, evseId, connId)) { connector =>
                        complete(ConnectorResp(GenericSuccess.code, None, data = connector)) }
                    }
                  }
              }
          }
      }
    }
  }
}

