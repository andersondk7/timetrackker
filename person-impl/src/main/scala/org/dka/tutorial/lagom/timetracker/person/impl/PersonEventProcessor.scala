package org.dka.tutorial.lagom.timetracker.person.impl

import java.sql.Connection

import com.lightbend.lagom.scaladsl.persistence.jdbc.JdbcReadSide
import com.lightbend.lagom.scaladsl.persistence.jdbc.JdbcSession.tryWith
import com.lightbend.lagom.scaladsl.persistence.{AggregateEventTag, EventStreamElement, ReadSideProcessor}

import scala.concurrent.ExecutionContext

/**
  * updates the ''query'' side from [[PersonEvent]] ''events'' generated from [[PersonCommand]]''commands''
  *
  * the [[jdbcReadSide]] provides the builder to create the [[com.lightbend.lagom.scaladsl.persistence.ReadSideProcessor.ReadSideHandler]]
  * this handler, once registered see [[PersonServiceImpl]] it will process [[PersonEvent]] instances by calling the
  * ''EventHander'' methods in this class.  The ''EventHandler'' methods are passed to the handler by calling ''setHandler'' method
  * on the builder provided by the [[jdbcReadSide]]
  *
  * @param jdbcReadSide  read side support
  * @param ec execution context in which the database calls will execute
  */
class PersonEventProcessor(jdbcReadSide: JdbcReadSide)(implicit ec: ExecutionContext) extends ReadSideProcessor[PersonEvent] {

  /*
   *  Event Handlers -- called when the [[PersonEvent]] is consumed.
   *
   *  these functions update the database used to support queries across [[Person]]s
   *
   *  naming convention processXYZ where 'XYZ' is the name of the event processed
   *
   */
  private def processPersonCreated(connection: Connection,
                                   eventElement: EventStreamElement[PersonCreated]): Unit = {
    tryWith(connection.prepareStatement(
      "INSERT INTO person_profile (id, name, email, textNumber) values(?, ?, ?, ?)")) { statement =>
      statement.setString(1, eventElement.event.id)
      statement.setString(2, eventElement.event.data.name)
      statement.setString(3, eventElement.event.data.email)
      statement.setString(4, eventElement.event.data.textNumber.getOrElse(""))
      statement.execute()
    }
  }

  private def processNameChanged(connection: Connection,
                                 eventElement: EventStreamElement[NameChanged]): Unit = {
    tryWith(connection.prepareStatement(
      "UPDATE person_profile SET name = ? where id = ?")) { statement =>
      statement.setString(1, eventElement.event.name)
      statement.setString(2, eventElement.event.id)
      statement.execute()
    }
  }

  private def processEmailChanged(connection: Connection,
                                  eventElement: EventStreamElement[EmailChanged]): Unit = {
    tryWith(connection.prepareStatement(
      "UPDATE person_profile SET email = ? where id = ?")) { statement =>
      statement.setString(1, eventElement.event.email)
      statement.setString(2, eventElement.event.id)
      statement.execute()
    }
  }

  private def processTextNumberChanged(connection: Connection,
                                       eventElement: EventStreamElement[TextNumberChanged]): Unit = {
    tryWith(connection.prepareStatement(
      "UPDATE person_profile SET textnumber = ? where id = ?")) { statement =>
      statement.setString(1, eventElement.event.textNumber.getOrElse(""))
      statement.setString(2, eventElement.event.id)
      statement.executeUpdate()
    }
  }

  /**
    * create the [[com.lightbend.lagom.scaladsl.persistence.ReadSideProcessor.ReadSideHandler]] instance
    * that will process the [[PersonEvent]]
    */
  override def buildHandler(): ReadSideProcessor.ReadSideHandler[PersonEvent] =
    jdbcReadSide.builder[PersonEvent]("personReadSideId")
      // since we create the tables manually, we do not need the setGlobalPrepare or the setPrepare methods
      //  so we just need to add a handler for every [[PersonEvent]] that causes a change on the ''query'' side
      .setEventHandler[PersonCreated](processPersonCreated)
      .setEventHandler[NameChanged](processNameChanged)
      .setEventHandler[EmailChanged](processEmailChanged)
      .setEventHandler[TextNumberChanged](processTextNumberChanged)
      .build()

  /**
    * since we are not sharding, there will be only '''one''' [[PersonEventProcessor]] and
    * all events will be processed sequentially
    *
    * @return all Tags for [[PersonEvent]]
    */
  override def aggregateTags: Set[AggregateEventTag[PersonEvent]] = Set(PersonEvent.PersonEventTag)
}
