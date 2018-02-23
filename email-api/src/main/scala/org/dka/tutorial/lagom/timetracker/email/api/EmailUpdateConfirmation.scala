package org.dka.tutorial.lagom.timetracker.email.api

/**
  * Confirmation of email updated
  */
case class EmailUpdateConfirmation(personId: String, email: String) {

}
