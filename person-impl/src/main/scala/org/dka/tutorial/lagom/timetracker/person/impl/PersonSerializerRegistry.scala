package org.dka.tutorial.lagom.timetracker.person.impl

import com.lightbend.lagom.scaladsl.playjson.{JsonSerializer, JsonSerializerRegistry}
import org.dka.tutorial.lagom.timetracker.person.api.PersonProfile

import scala.collection.immutable.Seq

/**
  * maps serializers to persisted classes
  */

object PersonSerializerRegistry extends JsonSerializerRegistry {
  override def serializers: Seq[JsonSerializer[_]] = Seq[JsonSerializer[_]](
    JsonSerializer[PersonProfile]
  )
}
