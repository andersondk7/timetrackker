package org.dka.tutorial.lagom.timetracker.person.impl

import java.util.UUID

import akka.NotUsed
import akka.event.slf4j.Logger
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.api.broker.Topic
import com.lightbend.lagom.scaladsl.broker.TopicProducer
import com.lightbend.lagom.scaladsl.persistence.jdbc.{JdbcReadSide, JdbcSession}
import com.lightbend.lagom.scaladsl.persistence.jdbc.JdbcSession.tryWith
import com.lightbend.lagom.scaladsl.persistence.{EventStreamElement, PersistentEntityRegistry, ReadSide}
import org.dka.tutorial.lagom.timetracker.person.api._

import scala.collection.immutable.VectorBuilder
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
  * @param persistentEntityRegistry how to find a given [[Person]] entity based on personId
  * @param readSide maps a [[com.lightbend.lagom.scaladsl.persistence.ReadSideProcessor]] for the [[Person]] entity
  * @param jdbcReadSide used in the implementation of the [[com.lightbend.lagom.scaladsl.persistence.ReadSideProcessor]]
  * @param jdbcSession used to query the [[JdbcReadSide]]
  */
class PersonServiceImpl(persistentEntityRegistry: PersistentEntityRegistry,
                        readSide: ReadSide,
                        jdbcReadSide: JdbcReadSide,
                        jdbcSession: JdbcSession
                       ) extends PersonService {
  private val logger = Logger.apply(this.getClass.getName)

  // associate a [[ReadSideProcessor]] that will handle the events created by the commands
  readSide.register[PersonEvent](new PersonEventProcessor(jdbcReadSide))

  private implicit def toOption(data: String): Option[String] = if (data.isEmpty) None else Some(data)

  // ---------------------------------------------------------
  // implementation functions
  // ---------------------------------------------------------
  override def profile(id: String): ServiceCall[NotUsed, PersonProfile] = ServiceCall { _ =>
    val person = persistentEntityRegistry.refFor[Person](id)
    person.ask(Profile)
  }

  override def create(): ServiceCall[PersonData, String] = ServiceCall { request =>
    val id = UUID.randomUUID().toString
    val person = persistentEntityRegistry.refFor[Person](id) // won't be there, so a new entry will be created
    person.ask(Create(id, request))
  }

  override def updateName(id: String, name: String): ServiceCall[NotUsed, PersonProfile] = ServiceCall { _ => // don't care about request
    val person = persistentEntityRegistry.refFor[Person](id)
    person.ask(ChangeName(name))
  }

  override def updateEmail(id: String, email: String): ServiceCall[NotUsed, PersonProfile] = ServiceCall { _ => // don't care about request
    val person = persistentEntityRegistry.refFor[Person](id)
    logger.info(s"TRACE:  updateEmail for $id")
    person.ask(ChangeEmail(email))
  }

  override def updateTextNumber(id: String, textNumber: String): ServiceCall[NotUsed, PersonProfile] = ServiceCall { _ => // don't care about request
    val person = persistentEntityRegistry.refFor[Person](id)
    person.ask(ChangeTextNumber(Some(textNumber)))
  }

  override def deleteTextNumber(id: String): ServiceCall[NotUsed, PersonProfile] = ServiceCall { _ => // don't care about request
    val person = persistentEntityRegistry.refFor[Person](id)
    person.ask(ChangeTextNumber(None))
  }

  /**
    * query side function
    * @return [[PersonProfile]] for all persons
    */
  override def profileSummaries: ServiceCall[NotUsed, Seq[PersonProfile]] = ServiceCall { _ => // don't care about request
    jdbcSession.withConnection { connection =>
      tryWith(connection.prepareStatement("SELECT id, name, email, textnumber from person_profile")) {
        ps =>
          tryWith(ps.executeQuery()) {rs =>
            val profiles = new VectorBuilder[PersonProfile]
            while (rs.next()) {
              profiles += PersonProfile(
                rs.getString("id"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("textNumber") // uses implicit defined in class
                )
            }
            profiles.result()
          }
      }
    }
  }

  // ---------------------------------------------------------
  // topics
  // ---------------------------------------------------------

  /**
    * listen for [[PersonEvent]] events and stream them to the [[PersonMessage]] topic
    * @return topic
    */
  override def personTopic() : Topic[PersonMessage] = {
    TopicProducer.singleStreamWithOffset { fromOffset =>
      persistentEntityRegistry.eventStream(PersonEvent.PersonEventTag, fromOffset)
        .map(ev => (convertEvent(ev), ev.offset))
    }
  }

  /**
    * convert the internal [[PersonEvent]] into an external [[PersonMessage]] published to other services
    *
    * @param eventElement event pulled from the stream, fired when person is created
    * @return [[PersonMessage]] message published in the topic for external consumption
    */
  private def convertEvent(eventElement: EventStreamElement[PersonEvent]): PersonMessage = {
    eventElement.event match {
      case PersonCreatedEvent(profile) =>
        logger.info(s"TRACE: converting from PersonCreated($profile)")
        PersonCreatedMessage(profile)
      case NameChangedEvent(profile) =>
        logger.info(s"TRACE: converting from NameChangedEvent($profile)")
        PersonNameUpdatedMessage(profile.id, profile.name)
      case EmailChangedEvent(profile) =>
        logger.info(s"TRACE: converting from EmailChangedEvent($profile)")
        PersonEmailUpdatedMessage(profile.id, profile.email)
      case TextNumberChangedEvent(profile) =>
        logger.info(s"TRACE: converting from TextNumberChangedEvent($profile)")
        PersonTextNumberUpdatedMessage(profile.id, profile.textNumber)
    }}

}


