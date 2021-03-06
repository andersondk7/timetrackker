package org.dka.tutorial.lagom.timetracker.person.impl

import org.dka.tutorial.lagom.timetracker.person.api._

import scala.language.implicitConversions

/**
  * persistent data for a person
  *
  * this is what is built from the events in the event store to get the current state of a [[Person]]
  *
  * @param id         globally unique personId of the person
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
  def apply(p: PersonProfile): PersonState = PersonState(p.id, p.name, p.email, p.textNumber)
}
