package org.dka.tutorial.lagom.timetracker.email.impl

import java.sql.{Connection, Timestamp}
import java.time.format.DateTimeFormatter

import com.lightbend.lagom.scaladsl.persistence.jdbc.JdbcReadSide
import com.lightbend.lagom.scaladsl.persistence.jdbc.JdbcSession.tryWith
import com.lightbend.lagom.scaladsl.persistence.{AggregateEventTag, EventStreamElement, ReadSideProcessor}

import scala.concurrent.ExecutionContext

/**
  * updates the ''query'' side from [[EmailEvent]] ''events'' generated from [[EmailCommand]] ''commands''
  *
  */
// todo: make this a cassandra implementation
class EmailEventProcessor(jdbcReadSide: JdbcReadSide)(implicit ec: ExecutionContext) extends ReadSideProcessor[EmailEvent] {
  private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

  override def buildHandler(): ReadSideProcessor.ReadSideHandler[EmailEvent] =
    jdbcReadSide.builder[EmailEvent]("emailReadSideId")
      .setEventHandler[EmailSent](emailSent)
      .build()

  private def emailSent(connection: Connection,
                        eventElement: EventStreamElement[EmailSent]
                       ): Unit = {
    tryWith(connection.prepareStatement(
      "INSERT INTO email_history (person_id, email_address, email_template, sent_at) values(?, ?, ?, ?)")) { statement =>
      statement.setString(1, eventElement.event.id)
      statement.setString(2, eventElement.event.address)
      statement.setString(3, eventElement.event.template)
      statement.setTimestamp(4, Timestamp.valueOf(eventElement.event.sentOn))
      statement.execute()
    }
  }

  override def aggregateTags: Set[AggregateEventTag[EmailEvent]] = Set(EmailEvent.EmailEventTag)

}
