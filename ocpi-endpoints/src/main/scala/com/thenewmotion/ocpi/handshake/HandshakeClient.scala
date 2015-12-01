package com.thenewmotion.ocpi.handshake

import akka.actor.ActorRefFactory
import com.thenewmotion.ocpi.common.OcpiClient
import com.thenewmotion.ocpi.handshake.Errors._
import com.thenewmotion.ocpi.msgs.v2_0.CommonTypes.SuccessResp
import com.thenewmotion.ocpi.msgs.v2_0.Credentials.Creds
import com.thenewmotion.ocpi.msgs.v2_0.Versions._
import spray.client.pipelining._
import spray.http._
import spray.httpx.SprayJsonSupport._
import scala.concurrent.{ExecutionContext, Future}
import scalaz.Scalaz._
import scalaz._

class HandshakeClient(implicit refFactory: ActorRefFactory) extends OcpiClient {
  import com.thenewmotion.ocpi.msgs.v2_0.OcpiJsonProtocol._

  def getVersions(uri: Uri, auth: String)(implicit ec: ExecutionContext): Future[HandshakeError \/ VersionsResp] = {
    val pipeline = request(auth) ~> unmarshalToOption[VersionsResp]
    pipeline(Get(uri)) map { toRight(_)(VersionsRetrievalFailed) }
  }

  def getVersionDetails(uri: Uri, auth: String)
    (implicit ec: ExecutionContext): Future[HandshakeError \/ VersionDetailsResp] = {
    val pipeline = request(auth) ~> unmarshalToOption[VersionDetailsResp]
    pipeline(Get(uri)) map { toRight(_)(VersionDetailsRetrievalFailed) }
  }

  def sendCredentials(uri: Uri, auth: String, creds: Creds)
    (implicit ec: ExecutionContext): Future[HandshakeError \/ SuccessResp] = {
    val pipeline = request(auth) ~> unmarshalToOption[SuccessResp]
    pipeline(Post(uri, creds)) map { toRight(_)(SendingCredentialsFailed) }
  }
}
