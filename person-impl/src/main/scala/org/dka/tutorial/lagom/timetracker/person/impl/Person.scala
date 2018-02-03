package org.dka.tutorial.lagom.timetracker.person.impl

import java.util.UUID

import akka.Done
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity.ReplyType
import org.dka.tutorial.lagom.timetracker.person.api.PersonProfile
import org.dka.tutorial.lagom.timetracker.person.impl.PersonCommand.NoReply
import play.libs.Json

/**
  * Entity
  */
class Person extends PersistentEntity {
  override type Command = PersonCommand
  override type Event = PersonEvent
  override type State = PersonState

  /**
    * used when there is no previous state
    */
  override def initialState: PersonState = PersonState.empty

  /**
    * what to do for a given command/event and state combination
    *
    * for [[Person]] all commands/events are valid for all states (it is not a finite state machine)
    * so there is only one set of [[Actions]]
    */
  override def behavior: Behavior = Actions() // create an empty Actions object and then add handlers
    /*
      all of the commands that change state
      flow ..
        do some business logic
        persist the event(s) associated with the command
        ctx.reply(...)
      */
    .onCommand[ChangeName, PersonProfile] {
      case (ChangeName(updatedName), ctx, state) =>
        // todo: send email/text to state.email and/or state.textNumber to let person know of the changes
        ctx.thenPersist(NameChanged(updatedName))(_ => ctx.reply(state.copy(name = updatedName)))
      }
    .onCommand[NoReply, Done] {
      case (ChangeEmail(updatedEmail), ctx, state) =>
        // todo: send email/text to state.email or updatedEmail and/or state.textNumber to let person know of the changes
        ctx.thenPersist(EmailChanged(updatedEmail))(_ => ctx.reply(Done))
      case (ChangeTextNumber(updatedTextNumber), ctx, state) =>
        // todo: send email/text to state.email and/or updatedTextNumber or state.textNumber to let person know of the changes
        ctx.thenPersist(TextNumberChanged(updatedTextNumber))(_ => ctx.reply(Done))
    }
    .onCommand[Create, String] {
      case (Create(id, data), ctx, state) =>
        ctx.thenPersist(PersonCreated(id, data))(_ => ctx.reply(id))
    }
    /*
     commands that don't change the state
       simply ctx.reply(...)
    */
    .onReadOnlyCommand[Profile.type, PersonProfile] {
      case (_, ctx, state) =>
        ctx.reply(PersonState.toProfile(state))
    }
    /*
     events on the entity
       return new state
     */
    .onEvent {
      case (NameChanged(updatedName), state) =>
        state.copy(name = updatedName)
      case (EmailChanged(updatedEmail), state) =>
        state.copy(email = updatedEmail)
      case (TextNumberChanged(updatedTextNumber), state) =>
        state.copy(textNumber = updatedTextNumber)
      case (PersonCreated(id, data), _) =>
        PersonState(id, data.name, data.email, data.textNumber)
  }
}

