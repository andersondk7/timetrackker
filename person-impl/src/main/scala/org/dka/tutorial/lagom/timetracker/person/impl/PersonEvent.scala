package org.dka.tutorial.lagom.timetracker.person.impl

import com.lightbend.lagom.scaladsl.persistence.{AggregateEvent, AggregateEventTag}
import com.lightbend.lagom.scaladsl.playjson.JsonSerializer
import org.dka.tutorial.lagom.timetracker.person.api._
import play.api.libs.json.Json

import scala.collection.immutable

/**
  * Events for a person
  */
sealed trait PersonEvent extends AggregateEvent[PersonEvent] {
  override def aggregateTag: AggregateEventTag[PersonEvent] = PersonEvent.PersonEventTag

  /**
    * @return personId of the person to whom the [[PersonEvent]] applies
    */
  def profile: PersonProfile
}


object PersonEvent {
  /**
    * enable read-side consumption of these events
    */
  val PersonEventTag: AggregateEventTag[PersonEvent] = AggregateEventTag[PersonEvent]
//  val PersonCreatedTag: AggregateEventTag[PersonCreatedEvent] = AggregateEventTag[PersonCreatedEvent]
//  val NameChangedTag: AggregateEventTag[NameChangedEvent] = AggregateEventTag[NameChangedEvent]
//  val EmailChangedTag: AggregateEventTag[EmailChangedEvent] = AggregateEventTag[EmailChangedEvent]
//  val TextNumberChangedTag: AggregateEventTag[TextNumberChangedEvent] = AggregateEventTag[TextNumberChangedEvent]

  /**
    * rather than put the serializers in the derived [[PersonEvent]] definitions,
    * put them all here so that:
    * if we change from Json to something else the change is limited to one place.
    * it is also easier to serialize the case objects
    * keeps code for [[PersonEvent]] implementations simpler because we don't need companion objects
    */
  val serializers: immutable.Seq[JsonSerializer[_]] = immutable.Seq(
    JsonSerializer(Json.format[PersonCreatedEvent]),
    JsonSerializer(Json.format[NameChangedEvent]),
    JsonSerializer(Json.format[EmailChangedEvent]),
    JsonSerializer(Json.format[TextNumberChangedEvent])
  )
}


final case class PersonCreatedEvent(override val profile: PersonProfile) extends PersonEvent {}

final case class NameChangedEvent(override val profile: PersonProfile) extends PersonEvent {}

final case class EmailChangedEvent(override val profile: PersonProfile) extends PersonEvent {}

final case class TextNumberChangedEvent(override val profile: PersonProfile) extends PersonEvent {}

