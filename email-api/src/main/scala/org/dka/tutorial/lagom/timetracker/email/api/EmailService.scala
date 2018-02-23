package org.dka.tutorial.lagom.timetracker.email.api

import akka.NotUsed
import com.lightbend.lagom.scaladsl.api.transport.Method
import com.lightbend.lagom.scaladsl.api.{Descriptor, Service, ServiceCall}

/**
  * defines email services
  */
trait EmailService extends Service {
  import Service._

  /**
    * defines the methods in the service
    *
    * map each endpoint with an implementing function
    */
  override final def descriptor: Descriptor =
    named("email")
      .withCalls(
        restCall(Method.POST, "/email/recipient/:id", sendEmail _),
        restCall(Method.GET, "/email/recipient/:id", emailAddress _)
      )
    .withAutoAcl(true)


  // ---------------------------------------------------------
  // implementation functions
  // ---------------------------------------------------------
  /**
    * send an email to the specified person, using the data in the [[Method.POST]]
    * @param id id of the person that will receive the email
    */
  def sendEmail(id: String): ServiceCall[EmailData, EmailSentConfirmation]

  def emailAddress(id: String): ServiceCall[NotUsed, String]


}
