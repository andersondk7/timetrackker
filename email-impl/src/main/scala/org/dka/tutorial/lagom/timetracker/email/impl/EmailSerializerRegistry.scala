package org.dka.tutorial.lagom.timetracker.email.impl

import com.lightbend.lagom.scaladsl.playjson.{JsonSerializer, JsonSerializerRegistry}
import org.dka.tutorial.lagom.timetracker.email.api.{EmailData, EmailSentConfirmation, EmailUpdateConfirmation}
import play.api.libs.json.Json

import scala.collection.immutable

/**
  * lumps all of the objects that are to be serialized together
  *
  * separate object so that it can be used in [[EmailServiceLoader]] as well as unit tests.
  */
object EmailSerializerRegistry extends JsonSerializerRegistry {
  override def serializers: immutable.Seq[JsonSerializer[_]] =
    EmailCommand.serializers ++
      EmailEvent.serializers ++
      immutable.Seq(
        JsonSerializer(Json.format[EmailSentConfirmation]),
        JsonSerializer(Json.format[EmailUpdateConfirmation]),
        JsonSerializer(Json.format[EmailData])
      )

}


