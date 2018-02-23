package org.dka.tutorial.lagom.timetracker.email.impl

import akka.{Done, NotUsed}
import akka.event.slf4j.Logger
import akka.stream.scaladsl.Flow
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.persistence.jdbc.{JdbcReadSide, JdbcSession}
import com.lightbend.lagom.scaladsl.persistence.{PersistentEntityRegistry, ReadSide}
import org.dka.tutorial.lagom.timetracker.email.api.{EmailData, EmailSentConfirmation, EmailService}
import org.dka.tutorial.lagom.timetracker.person.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.language.implicitConversions

/**
  * implementation of service
  *
  * in true CQRS and event sourcing:
  * 1. '''commands''' generate '''events '''
  * 1. events are replayed to get the current state of an object (ie. entity)
  * 1. events are also processed to generate the data used by the '''query''' side
  *
  * @param persistentEntityRegistry how to find a given [[Recipient]] entity based on id
  * @param readSide                 maps a [[com.lightbend.lagom.scaladsl.persistence.ReadSideProcessor]] for the [[Recipient]] entity
  * @param jdbcReadSide             used in the implementation of the [[com.lightbend.lagom.scaladsl.persistence.ReadSideProcessor]]
  * @param jdbcSession              used to query the [[JdbcReadSide]]
  */
class EmailServiceImpl(persistentEntityRegistry: PersistentEntityRegistry,
                       readSide: ReadSide,
                       jdbcReadSide: JdbcReadSide,
                       jdbcSession: JdbcSession,
                       personService: PersonService
                      ) extends EmailService {

  private val logger = Logger.apply(this.getClass.getName)

  // associate a [[ReadSideProcessor]] that will handle the events created by the commands
  readSide.register[EmailEvent](new EmailEventProcessor(jdbcReadSide))

  personService
    .personTopic()
    .subscribe
    .atLeastOnce(
      Flow[PersonMessage].map {
        case PersonCreatedMessage(profile) =>
          logger.info(s"TRACE:  ${profile.id} created with $profile")
          val recipient = persistentEntityRegistry.refFor[Recipient](profile.id)
          recipient.ask(CreateRecipient(profile.id, profile.email))
          Done
        case PersonUpdatedMessage(profile) =>
          logger.info(s"TRACE:  ${profile.id} updated to $profile")
          val recipient = persistentEntityRegistry.refFor[Recipient](profile.id)
          recipient.ask(ChangeEmail(profile.email))
          Done
        case PersonEmailUpdatedMessage(id, updatedEmailAddress) =>
          logger.info(s"TRACE:  $id updated email to $updatedEmailAddress")
          val recipient = persistentEntityRegistry.refFor[Recipient](id)
          recipient.ask(ChangeEmail(updatedEmailAddress))
          Done
        case PersonNameUpdatedMessage(id, updatedName) =>
          logger.info(s"TRACE:  $id updated name to $updatedName")
          val recipient = persistentEntityRegistry.refFor[Recipient](id)
          recipient.ask(ChangeName(updatedName))
          Done
        case PersonTextNumberUpdatedMessage(id, updatedTextNumber) =>
          logger.info(s"TRACE:  $id updated textNumber to $updatedTextNumber")
          val recipient = persistentEntityRegistry.refFor[Recipient](id)
          recipient.ask(ChangeTextNumber(updatedTextNumber))
          Done
      }
    )

  // ---------------------------------------------------------
  // implementation functions
  // ---------------------------------------------------------

  override def sendEmail(id: String): ServiceCall[EmailData, EmailSentConfirmation] = ServiceCall { request =>
    val recipient = persistentEntityRegistry.refFor[Recipient](id)
    recipient.ask(SendEmail(request.template, request.data))
  }

  override def emailAddress(id: String): ServiceCall[NotUsed, String] = ServiceCall { request =>
    val recipient = persistentEntityRegistry.refFor[Recipient](id)
    recipient.ask(GetEmailAddress)
  }
}


