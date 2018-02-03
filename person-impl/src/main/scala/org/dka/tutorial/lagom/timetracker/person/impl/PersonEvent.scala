package org.dka.tutorial.lagom.timetracker.person.impl

import com.lightbend.lagom.scaladsl.persistence.{AggregateEvent, AggregateEventTag}
import com.lightbend.lagom.scaladsl.playjson.JsonSerializer
import org.dka.tutorial.lagom.timetracker.person.api.{PersonData, PersonProfile}
import play.api.libs.json.Json

import scala.collection.immutable

/**
  * Events for a person
  */
sealed trait PersonEvent extends AggregateEvent[PersonEvent] {
  def aggregateTag: AggregateEventTag[PersonEvent] = PersonEvent.Tag
}

object PersonEvent {
  val Tag: AggregateEventTag[PersonEvent] = AggregateEventTag[PersonEvent]
  /**
    * rather than put the serializers in the derived [[PersonCommand]] definitions,
    * put them all here so that if we change from Json to something else the change is
    * limited to one place.  (it is also easier to serialize the case objects here)
    */
  val serializers: immutable.Seq[JsonSerializer[_]] = immutable.Seq(
    JsonSerializer(Json.format[NameChanged]),
    JsonSerializer(Json.format[EmailChanged]),
    JsonSerializer(Json.format[TextNumberChanged]),
    JsonSerializer(Json.format[PersonCreated])
  )
}

final case class NameChanged(name: String) extends PersonEvent

final case class EmailChanged(email: String) extends PersonEvent

final case class TextNumberChanged(textNumber: Option[String]) extends PersonEvent

final case class PersonCreated(id: String, data: PersonData) extends PersonEvent
