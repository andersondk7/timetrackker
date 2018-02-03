package org.dka.tutorial.lagom.timetracker.person.api

import play.api.libs.json.{Format, Json}

/**
  * api (ie. wire) representation profile information on a person
  *
  * @param id globally unique id of the person
  * @param name name by which the person is known to others
  * @param email email address of person
  * @param textNumber optional sms text number
  */
case class PersonProfile(id: String, name: String, email: String, textNumber: Option[String] = None) {
}

object PersonProfile {
  /**
    * wire representation as Json
    */
  implicit val format: Format[PersonProfile] = Json.format[PersonProfile]
}
