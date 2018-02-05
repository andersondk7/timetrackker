package org.dka.tutorial.lagom.timetracker.person.impl

import java.util.UUID

import akka.NotUsed
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.persistence.PersistentEntityRegistry
import org.dka.tutorial.lagom.timetracker.person.api.{PersonData, PersonProfile, PersonService}

/**
  * implementation of service
  */
class PersonServiceImpl(persistentEntityRegistry: PersistentEntityRegistry) extends PersonService {


  override def profile(id: String): ServiceCall[NotUsed, PersonProfile] = ServiceCall { _ =>
    val person = persistentEntityRegistry.refFor[Person](id)
    person.ask(Profile)
  }

  override def create(): ServiceCall[PersonData, String] = ServiceCall { request =>
    val id = UUID.randomUUID().toString
    val person = persistentEntityRegistry.refFor[Person](id) // won't be there, so a new entry will be created
    person.ask(Create(id, request))
  }

  override def updateName(id: String, name: String): ServiceCall[NotUsed, PersonProfile] = ServiceCall { _ =>
    val person = persistentEntityRegistry.refFor[Person](id)
    person.ask(ChangeName(name))
  }

  override def updateEmail(id: String, email: String): ServiceCall[NotUsed, PersonProfile] = ServiceCall { _ =>
    val person = persistentEntityRegistry.refFor[Person](id)
    person.ask(ChangeEmail(email))
  }

  override def updateTextNumber(id: String, textNumber: String): ServiceCall[NotUsed, PersonProfile] = ServiceCall { _ =>
    val person = persistentEntityRegistry.refFor[Person](id)
    person.ask(ChangeTextNumber(Some(textNumber)))
  }

  override def deleteTextNumber(id: String): ServiceCall[NotUsed, PersonProfile] = ServiceCall { _ =>
    val person = persistentEntityRegistry.refFor[Person](id)
    person.ask(ChangeTextNumber(None))
  }
}


