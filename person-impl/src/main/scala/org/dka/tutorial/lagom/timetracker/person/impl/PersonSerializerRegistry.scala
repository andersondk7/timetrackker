package org.dka.tutorial.lagom.timetracker.person.impl

import com.lightbend.lagom.scaladsl.playjson.{JsonSerializer, JsonSerializerRegistry}
import org.dka.tutorial.lagom.timetracker.person.api._
import play.api.libs.json.Json

import scala.collection.immutable


/**
  * lumps all of the objects that are to be serialized together
  *
  * separate object so that it can be used in [[PersonServiceLoader]] as well as unit tests.
  */
object PersonSerializerRegistry extends JsonSerializerRegistry {
  override def serializers: immutable.Seq[JsonSerializer[_]] =
    PersonCommand.serializers ++
      PersonEvent.serializers ++
      immutable.Seq(
        JsonSerializer(Json.format[PersonState]),
        JsonSerializer(Json.format[PersonProfile])
      )
}
