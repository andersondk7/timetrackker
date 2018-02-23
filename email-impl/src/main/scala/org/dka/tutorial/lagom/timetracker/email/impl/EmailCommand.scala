package org.dka.tutorial.lagom.timetracker.email.impl

import akka.NotUsed
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity.ReplyType
import com.lightbend.lagom.scaladsl.playjson.JsonSerializer
import com.lightbend.lagom.scaladsl.playjson.JsonSerializer.emptySingletonFormat
import org.dka.tutorial.lagom.timetracker.email.api.{EmailSentConfirmation, EmailUpdateConfirmation}

import scala.collection.immutable

/**
  * Commands for the [[Recipient]]
  */
sealed trait EmailCommand {}

object EmailCommand {

  import play.api.libs.json._

  /**
    * rather than put the serializers in the derived [[EmailCommand]] definitions,
    * put them all here so that:
    * if we change from Json to something else the change is limited to one place.
    * it is also easier to serialize the case objects
    * keeps code for [[EmailCommand]] implementations simpler because we don't need companion objects
    */
  val serializers: immutable.Seq[JsonSerializer[_]] = immutable.Seq(
    JsonSerializer(emptySingletonFormat(GetEmailAddress)),
    JsonSerializer(Json.format[CreateRecipient]),
    JsonSerializer(Json.format[ChangeEmail]),
    JsonSerializer(Json.format[ChangeName]),
    JsonSerializer(Json.format[ChangeTextNumber]),
    JsonSerializer(Json.format[SendEmail])
  )

}

//
// commands
//
final case class CreateRecipient(id: String, email: String) extends EmailCommand with ReplyType[NotUsed]

final case class ChangeEmail(email: String) extends EmailCommand with ReplyType[NotUsed]

final case class ChangeName(name: String) extends EmailCommand with ReplyType[NotUsed]

final case class ChangeTextNumber(textNumber: Option[String]) extends EmailCommand with ReplyType[NotUsed]

final case class SendEmail(template: String, data: Map[String, String]) extends EmailCommand with ReplyType[EmailSentConfirmation]

case object GetEmailAddress extends EmailCommand with ReplyType[String]
