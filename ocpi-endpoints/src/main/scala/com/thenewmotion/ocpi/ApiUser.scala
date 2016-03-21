package com.thenewmotion.ocpi

import com.thenewmotion.mobilityid.PartyId

case class ApiUser(
  id: String,
  token: String,
  party: PartyId
)