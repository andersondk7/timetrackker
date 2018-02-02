package org.dka.tutorial.lagom.timetracker.person.api

import akka.NotUsed
import com.lightbend.lagom.scaladsl.api.{Descriptor, Service, ServiceCall}

/**
  *
  */
trait PersonService extends Service {

  def profile(id: String): ServiceCall[NotUsed, PersonProfile]

  override final def descriptor: Descriptor = {
    import Service._
    named("person")
      .withCalls(
        pathCall("/person/profile/:id", profile _)
      )
      .withAutoAcl(true)
  }
}

