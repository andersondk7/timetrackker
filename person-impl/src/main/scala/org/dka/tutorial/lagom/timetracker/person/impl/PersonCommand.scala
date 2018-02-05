package org.dka.tutorial.lagom.timetracker.person.impl

import com.lightbend.lagom.scaladsl.persistence.PersistentEntity.ReplyType
import com.lightbend.lagom.scaladsl.playjson.JsonSerializer
import org.dka.tutorial.lagom.timetracker.person.api.{PersonData, PersonProfile}

import scala.collection.immutable

/**
  * Commands for the [[Person]]
  *
  */
sealed trait PersonCommand {}

sealed trait UpdateCommand extends PersonCommand with ReplyType[PersonProfile]

object PersonCommand {

  import JsonSerializer.emptySingletonFormat
  import play.api.libs.json._

  /**
    * rather than put the serializers in the derived [[PersonCommand]] definitions,
    * put them all here so that:
    * if we change from Json to something else the change is limited to one place.
    * it is also easier to serialize the case objects
    * keeps code for [[PersonCommand]] implementations simpler because we don't need companion objects
    */
  val serializers: immutable.Seq[JsonSerializer[_]] = immutable.Seq(
    JsonSerializer(emptySingletonFormat(Profile)),
    JsonSerializer(Json.format[Create]),
    JsonSerializer(Json.format[ChangeName]),
    JsonSerializer(Json.format[ChangeEmail]),
    JsonSerializer(Json.format[ChangeTextNumber])
  )
}

//
// commands
//

case object Profile extends PersonCommand with ReplyType[PersonProfile]

final case class Create(id: String, data: PersonData) extends PersonCommand with ReplyType[String]

final case class ChangeName(name: String) extends UpdateCommand

final case class ChangeEmail(email: String) extends UpdateCommand

final case class ChangeTextNumber(textNumber: Option[String]) extends UpdateCommand

