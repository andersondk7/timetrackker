package org.dka.tutorial.lagom.timetracker.person.api

import play.api.libs.json._

/**
  * represents messages sent out to other services
  */
sealed trait PersonMessage {
  def personId: String
}

object PersonMessage {
  implicit val format: Format[PersonMessage] = Format[PersonMessage] (
    Reads { js =>
      val messageType = (JsPath \ "messageType").read[String].reads(js)
      messageType.fold(
        _ => JsError("messageType undefined or incorrect"), {
          case PersonCreatedMessage.messageType => (JsPath \ "data").read[PersonCreatedMessage].reads(js)
          case PersonUpdatedMessage.messageType => (JsPath \ "data").read[PersonUpdatedMessage].reads(js)
          case PersonEmailUpdatedMessage.messageType => (JsPath \ "data").read[PersonEmailUpdatedMessage].reads(js)
          case PersonNameUpdatedMessage.messageType => (JsPath \ "data").read[PersonNameUpdatedMessage].reads(js)
          case PersonTextNumberUpdatedMessage.messageType => (JsPath \ "data").read[PersonTextNumberUpdatedMessage].reads(js)
        }
      )
    },
    Writes {
      case m: PersonCreatedMessage =>
        JsObject(Seq(
          "messageType" -> JsString(PersonCreatedMessage.messageType),
          "data" -> PersonCreatedMessage.format.writes(m)
        ))
      case m: PersonUpdatedMessage =>
        JsObject(Seq(
          "messageType" -> JsString(PersonUpdatedMessage.messageType),
          "data" -> PersonUpdatedMessage.format.writes(m)
        ))
      case m: PersonEmailUpdatedMessage =>
        JsObject(Seq(
          "messageType" -> JsString(PersonEmailUpdatedMessage.messageType),
          "data" -> PersonEmailUpdatedMessage.format.writes(m)
        ))
      case m: PersonNameUpdatedMessage =>
        JsObject(Seq(
          "messageType" -> JsString(PersonNameUpdatedMessage.messageType),
          "data" -> PersonNameUpdatedMessage.format.writes(m)
        ))
      case m: PersonTextNumberUpdatedMessage =>
        JsObject(Seq(
          "messageType" -> JsString(PersonTextNumberUpdatedMessage.messageType),
          "data" -> PersonTextNumberUpdatedMessage.format.writes(m)
        ))
    }
  )
}

case class PersonCreatedMessage(profile: PersonProfile) extends PersonMessage { override val personId: String = profile.id}

object PersonCreatedMessage {
  implicit val format: Format[PersonCreatedMessage] = Json.format[PersonCreatedMessage]
  val messageType = "personCreated"
}

case class PersonUpdatedMessage(profile: PersonProfile) extends PersonMessage {override val personId: String = profile.id }

object PersonUpdatedMessage {
  implicit val format: Format[PersonUpdatedMessage] = Json.format[PersonUpdatedMessage]
  val messageType = "personUpdated"
}

case class PersonEmailUpdatedMessage(override val personId: String, updatedEmailAddress: String) extends PersonMessage { }

object PersonEmailUpdatedMessage {
  implicit val format: Format[PersonEmailUpdatedMessage] = Json.format[PersonEmailUpdatedMessage]
  val messageType = "personEmailUpdated"
}

case class PersonNameUpdatedMessage(override val personId: String, updatedNameAddress: String) extends PersonMessage { }

object PersonNameUpdatedMessage {
  implicit val format: Format[PersonNameUpdatedMessage] = Json.format[PersonNameUpdatedMessage]
  val messageType = "personNameUpdated"
}

case class PersonTextNumberUpdatedMessage(override val personId: String, updatedTextNumber: Option[String]) extends PersonMessage { }

object PersonTextNumberUpdatedMessage {
  implicit val format: Format[PersonTextNumberUpdatedMessage] = Json.format[PersonTextNumberUpdatedMessage]
  val messageType = "personTextNumberUpdated"
}






