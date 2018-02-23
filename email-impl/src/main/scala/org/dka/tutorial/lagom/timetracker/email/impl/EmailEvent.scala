package org.dka.tutorial.lagom.timetracker.email.impl


import java.time.LocalDateTime

import com.lightbend.lagom.scaladsl.persistence.{AggregateEvent, AggregateEventTag}
import com.lightbend.lagom.scaladsl.playjson.JsonSerializer
import play.api.libs.json.Json

import scala.collection.immutable

/**
  * Events for a person
  */
sealed trait EmailEvent extends AggregateEvent[EmailEvent] {
  override def aggregateTag: AggregateEventTag[EmailEvent] = EmailEvent.EmailEventTag

  /**
    * @return id of the person to whom the [[EmailEvent]] applies
    */
  def id: String
}

object EmailEvent {
  /**
    * enable read-side consumption of these events
    *
    * for now, consume events sequentially, add shards later
    *
    */
  val EmailEventTag: AggregateEventTag[EmailEvent] = AggregateEventTag[EmailEvent]

  /**
    * rather than put the serializers in the derived [[EmailEvent]] definitions,
    * put them all here so that:
    * if we change from Json to something else the change is limited to one place.
    * it is also easier to serialize the case objects
    * keeps code for [[EmailEvent]] implementations simpler because we don't need companion objects
    */
  val serializers: immutable.Seq[JsonSerializer[_]] = immutable.Seq(
    JsonSerializer(Json.format[RecipientCreated]),
    JsonSerializer(Json.format[EmailChanged]),
    JsonSerializer(Json.format[EmailSent])
  )
}


final case class RecipientCreated(override val id: String, email: String) extends EmailEvent

final case class EmailChanged(override val id: String, email: String) extends EmailEvent

final case class EmailSent(override val id: String, address: String, template: String, data: Map[String, String], sentOn: LocalDateTime) extends EmailEvent



