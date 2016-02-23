package com.thenewmotion.ocpi

import java.util.Locale

case class ApiUser(
  id: String,
  token: String,
  countryCode: String,
  partyId: String
) {
  require(countryCode.length == 2 && countryCode.matches("""[A-Za-z]{2}"""), "Country code needs to conform to ISO 3166-1 alpha-2")
  require(Locale.getISOCountries.contains(countryCode))
  require(partyId.length == 3 && partyId.matches("""\w{3}"""), "Party ID needs to conform to ISO 15118")
}