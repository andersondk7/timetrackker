package org.dka.tutorial.lagom.timetracker.person.impl

import com.lightbend.lagom.scaladsl.persistence.{AggregateEvent, AggregateEventTag}
import com.lightbend.lagom.scaladsl.playjson.JsonSerializer
import org.dka.tutorial.lagom.timetracker.person.api.PersonData
import play.api.libs.json.Json

import scala.collection.immutable

/**
  * Events for a person
  */
sealed trait PersonEvent extends AggregateEvent[PersonEvent] {
  override def aggregateTag: AggregateEventTag[PersonEvent] = PersonEvent.PersonEventTag

  /**
    * @return id of the person to whom the [[PersonEvent]] applies
    */
  def id: String
}

object PersonEvent {
  /**
    * enable read-side consumption of these events
    *
    * for now, consume events sequentially, add shards later
    *
    */
  val PersonEventTag: AggregateEventTag[PersonEvent] = AggregateEventTag[PersonEvent]

  /**
    * rather than put the serializers in the derived [[PersonEvent]] definitions,
    * put them all here so that:
    * if we change from Json to something else the change is limited to one place.
    * it is also easier to serialize the case objects
    * keeps code for [[PersonEvent]] implementations simpler because we don't need companion objects
    */
  val serializers: immutable.Seq[JsonSerializer[_]] = immutable.Seq(
    JsonSerializer(Json.format[PersonCreated]),
    JsonSerializer(Json.format[NameChanged]),
    JsonSerializer(Json.format[EmailChanged]),
    JsonSerializer(Json.format[TextNumberChanged])
  )
}


final case class PersonCreated(override val id: String, data: PersonData) extends PersonEvent

final case class NameChanged(override val id: String, name: String) extends PersonEvent

final case class EmailChanged(override val id: String, email: String) extends PersonEvent

final case class TextNumberChanged(override val id: String, textNumber: Option[String]) extends PersonEvent

