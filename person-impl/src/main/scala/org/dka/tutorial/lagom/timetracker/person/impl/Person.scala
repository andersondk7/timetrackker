package org.dka.tutorial.lagom.timetracker.person.impl

import com.lightbend.lagom.scaladsl.persistence.PersistentEntity
import org.dka.tutorial.lagom.timetracker.person.api.PersonProfile

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
  // @formatter:off
  override def behavior: Behavior = Actions() // create an empty Actions object and then add handlers
    /*
      all of the commands that change state have this flow ...
        do some business logic
        persist the event(s) associated with the command
        ctx.reply(...) back to the caller ([[PersonServiceImpl]]
      */
    .onCommand[Create, String] {
      case (Create(id, data), ctx, _) =>
        ctx.thenPersist(PersonCreated(id, data))(_ => ctx.reply(id))
      }
    .onCommand[ChangeName, PersonProfile] {
      case (ChangeName(updatedName), ctx, state) =>
        ctx.thenPersist(NameChanged(state.id, updatedName))(_ => ctx.reply(state.copy(name = updatedName)))
      }
    .onCommand[ChangeEmail, PersonProfile] {
      case (ChangeEmail(updatedEmail), ctx, state) =>
        ctx.thenPersist(EmailChanged(state.id, updatedEmail))(_ => ctx.reply(state.copy(email = updatedEmail)))
      }
    .onCommand[ChangeTextNumber, PersonProfile] {
      case (ChangeTextNumber(updatedTextNumber), ctx, state) =>
        ctx.thenPersist(TextNumberChanged(state.id, updatedTextNumber))(_ => ctx.reply(state.copy(textNumber = updatedTextNumber)))
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
      case (NameChanged(id, updatedName), state) =>
        state.copy(name = updatedName)
      case (EmailChanged(id, updatedEmail), state) =>
        state.copy(email = updatedEmail)
      case (TextNumberChanged(id, updatedTextNumber), state) =>
        state.copy(textNumber = updatedTextNumber)
      case (PersonCreated(id, data), _) =>
        PersonState(id, data.name, data.email, data.textNumber)
    }
  // @formatter:on
}

