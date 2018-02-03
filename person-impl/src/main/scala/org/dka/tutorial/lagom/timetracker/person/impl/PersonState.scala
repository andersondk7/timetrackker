package org.dka.tutorial.lagom.timetracker.person.impl

import java.util.UUID

import org.dka.tutorial.lagom.timetracker.person.api.PersonProfile

import scala.language.implicitConversions

/**
  * persistent data for a person
  *
  * @param id         globally unique id of the person
  * @param name       name by which the person is known to others
  * @param email      email address of person
  * @param textNumber optional sms text number
  */
final case class PersonState(
                              id: String, //todo: make this a UUID
                              name: String,
                              email: String,
                              textNumber: Option[String] = None) {
}

object PersonState {
  val initialId = "na"
  val empty: PersonState = {
    PersonState(initialId, initialId, "unknown@somewhere.com")
  }

  implicit def toProfile(ps: PersonState): PersonProfile = PersonProfile(ps.id, ps.name, ps.email, ps.textNumber)
}
