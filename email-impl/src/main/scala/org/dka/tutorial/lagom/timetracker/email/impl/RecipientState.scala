package org.dka.tutorial.lagom.timetracker.email.impl

/**
  * persistent data for a person
  *
  * this is what is built from the events in the event store to get the current state of a [[Recipient]]
  *
  * @param id    globally unique id of the person
  * @param email email address of person
  */
final case class RecipientState(
                                 id: String, //todo: make this a UUID
                                 email: String
                               ) {
}

object RecipientState {
  val initialId = "na"
  val empty: RecipientState = {
    RecipientState(initialId, "unknown@somewhere.com")
  }
}
