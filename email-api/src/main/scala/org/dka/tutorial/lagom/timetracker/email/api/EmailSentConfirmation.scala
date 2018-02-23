package org.dka.tutorial.lagom.timetracker.email.api

import java.time.LocalDateTime

import play.api.libs.json.{Format, Json}

/**
  * confirmation that an email was sent
  */
case class EmailSentConfirmation(personId: String, template: String, date: LocalDateTime) {}

object EmailSentConfirmation {
  implicit val format: Format[EmailSentConfirmation] = Json.format[EmailSentConfirmation]
}