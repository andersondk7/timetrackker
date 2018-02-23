package org.dka.tutorial.lagom.timetracker.person.impl

import com.lightbend.lagom.scaladsl.api.transport.{ExceptionMessage, TransportErrorCode, TransportException}

/**
  * exceptions thrown in the application
  *
  * children classes that also derive from [[TransportException]] can be returned to the client with the corresponding error code
  *
  */
sealed trait PersonServiceException extends Exception {
  override lazy val getMessage: String = reason

  def reason: String
}

case class InternalError(id: String, cause: Exception) extends PersonServiceException {
  override val reason: String = s"personId: $id caused exception: $cause"
}

case class PersonNotFoundException(id: String)
  extends TransportException(TransportErrorCode.NotFound, new ExceptionMessage(PersonNotFoundException.getClass.getSimpleName, s"person with personId: $id does not exist"))
    with PersonServiceException {
  override val reason: String = this.exceptionMessage.name
}


