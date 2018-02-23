package org.dka.tutorial.lagom.timetracker.person.impl

import com.lightbend.lagom.scaladsl.persistence.PersistentEntity
import org.dka.tutorial.lagom.timetracker.person.api.PersonProfile
import akka.event.slf4j.Logger

/**
  * Entity
  */
class Person extends PersistentEntity {
  override type Command = PersonCommand
  override type Event = PersonEvent
  override type State = PersonState

  private val logger = Logger.apply(this.getClass.getName)

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
        val profile = PersonProfile(id, data)
        ctx.thenPersist(PersonCreatedEvent(profile))(_ => ctx.reply(id))
      }
    .onCommand[ChangeName, PersonProfile] {
      case (ChangeName(updatedName), ctx, state) =>
        val updated = state.copy(name = updatedName)
        ctx.thenPersist(NameChangedEvent(updated))(_ => ctx.reply(updated))
      }
    .onCommand[ChangeEmail, PersonProfile] {
      case (ChangeEmail(updatedEmail), ctx, state) =>
        val updated = state.copy(email = updatedEmail)
        logger.info(s"TRACE:  ChangeEmail ($updatedEmail on state: $state")
        ctx.thenPersist(EmailChangedEvent(updated))(_ => ctx.reply(updated))
      }
    .onCommand[ChangeTextNumber, PersonProfile] {
      case (ChangeTextNumber(updatedTextNumber), ctx, state) =>
        val updated = state.copy(textNumber = updatedTextNumber)
        ctx.thenPersist(TextNumberChangedEvent(updated))(_ => ctx.reply(updated))
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
      case (NameChangedEvent(profile), state) =>
        state.copy(name = profile.name)
      case (EmailChangedEvent(profile), state) =>
        logger.info(s"TRACE:  EmailChanged ($profile) on state: $state")
        state.copy(email = profile.email)
      case (TextNumberChangedEvent(profile), state) =>
        state.copy(textNumber = profile.textNumber)
      case (PersonCreatedEvent(profile), _) =>
        logger.info(s"TRACE: PersonCreated($profile)")
        PersonState(profile)
    }
  // @formatter:on
}

