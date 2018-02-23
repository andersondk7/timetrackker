package org.dka.tutorial.lagom.timetracker.email.impl


import java.time.LocalDateTime

import akka.NotUsed
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity
import org.dka.tutorial.lagom.timetracker.email.api.{EmailSentConfirmation, EmailUpdateConfirmation}

/**
  * Entity
  */
class Recipient(emailService: EmailDeliveryService) extends PersistentEntity {
  override type Command = EmailCommand
  override type Event = EmailEvent
  override type State = RecipientState

  /**
    * used when there is no previous state
    */
  override def initialState: RecipientState = RecipientState.empty


  /**
    * what to do for a given command/event and state combination
    *
    * for [[Recipient]] all commands/events are valid for all states (it is not a finite state machine)
    * so there is only one set of [[Actions]]
    */
  // @formatter:off
  override def behavior: Behavior = Actions() // create an empty Actions object and then add handlers
    /*
      all of the commands that change state have this flow ...
        do some business logic
        persist the event(s) associated with the command
        ctx.reply(...) back to the caller ([[EmailServiceImpl]]
      */
    .onCommand[CreateRecipient, NotUsed] {
      case (CreateRecipient(id, email), ctx, _) =>
        ctx.thenPersist(RecipientCreated(id, email))(_ => ctx.reply(NotUsed))
      }
    .onCommand[ChangeEmail, NotUsed] {
      case (ChangeEmail(updatedEmail), ctx, state) =>
        val changedEmailNotification = sendEmailChanged(state.id, state.email, updatedEmail)
        ctx.thenPersistAll(
          changedEmailNotification,
          EmailChanged(state.id, updatedEmail)
          ) ( () => ctx.reply(NotUsed))
      }
    .onCommand[ChangeName, NotUsed] {
      case (ChangeName(updatedName), ctx, state) =>
        val changedNameNotification = sendNameChanged(state.id, state.email, updatedName)
        ctx.thenPersist(changedNameNotification) ( _ => ctx.reply(NotUsed))
      }
    .onCommand[ChangeTextNumber, NotUsed] {
      case (ChangeTextNumber(updatedTextNumber), ctx, state) =>
        val changedTextNumberNotification = sendTextNumberChanged(state.id, state.email, updatedTextNumber)
        ctx.thenPersist(changedTextNumberNotification) ( _ => ctx.reply(NotUsed))
      }
      .onCommand[SendEmail, EmailSentConfirmation] {
      case (SendEmail(template, data), ctx, state) =>
        val sentOn = emailService.send(state.email, template, data)
        // persist the event (so that listeners (such as the read side) can respond
        ctx.thenPersist(EmailSent(state.id, state.email, template, data, sentOn))(_ => ctx.reply(EmailSentConfirmation(state.id, template, sentOn)))
      }
    /*
     commands that don't change the state
       simply ctx.reply(...)
    */
      .onReadOnlyCommand[GetEmailAddress.type, String] {
        case (_, ctx, state) => ctx.reply(state.email)
      }
    /*
     events on the entity
       return new state
     */
    .onEvent {
      case (RecipientCreated(id, email), _) =>
        RecipientState(id, email)
      case (EmailChanged(_, updatedEmail), state) =>
        state.copy(email = updatedEmail)
      case (EmailSent(_, _, _, _, _), state) =>
        state // we don't track emails sent as part of the Recipient entity

    }
  // @formatter:on

  def sendEmailChanged(recipientId: String, oldEmail: String, newEmail: String): EmailSent = {
    val template = "changedEmailTemplate"
    val data = Map("oldEmail" -> oldEmail, "newEmail" -> newEmail)
    val sentOn = emailService.send(oldEmail, template, data)
    EmailSent(recipientId, oldEmail, template, data, sentOn)
  }

  def sendNameChanged(recipientId: String, email: String, newName: String): EmailSent = {
    val template = "changedNameTemplate"
    val data = Map("newName" -> newName)
    val sentOn = emailService.send(email, template, data)
    EmailSent(recipientId, email, template, data, sentOn)
  }

  def sendTextNumberChanged(recipientId: String, email: String, newTextNumber: Option[String]): EmailSent = {
    val template = "changedTextNumberTemplate"
    val data = Map("newTextNumber" -> newTextNumber.getOrElse(""))
    val sentOn = emailService.send(email, template, data)
    EmailSent(recipientId, email, template, data, sentOn)
  }
}
