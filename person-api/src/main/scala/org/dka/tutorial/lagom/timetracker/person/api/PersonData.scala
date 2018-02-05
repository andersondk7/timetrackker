package org.dka.tutorial.lagom.timetracker.person.api

import play.api.libs.json.{Format, Json}

/**
  * api (ie. wire) representation profile information to create a person
  *
  * @param name       name by which the person is known to others
  * @param email      email address of person
  * @param textNumber optional sms text number
  */
case class PersonData(name: String, email: String, textNumber: Option[String] = None) {
}

object PersonData {
  /**
    * wire representation as Json
    */
  implicit val format: Format[PersonData] = Json.format[PersonData]
}
