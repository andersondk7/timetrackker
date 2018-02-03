package org.dka.tutorial.lagom.timetracker.person.api

import akka.{Done, NotUsed}
import com.lightbend.lagom.scaladsl.api.transport.Method
import com.lightbend.lagom.scaladsl.api.{Descriptor, Service, ServiceCall}

/**
  *
  */
trait PersonService extends Service {

  def profile(id: String): ServiceCall[NotUsed, PersonProfile]

  def create(): ServiceCall[PersonData, String]

  def updateName(id: String, name: String): ServiceCall[NotUsed, PersonProfile]

  override final def descriptor: Descriptor = {
    import Service._
    named("person")
      .withCalls(
        pathCall("/person/profile/:id", profile _),
        restCall(Method.POST, "/person", create _),
        restCall(Method.PUT, "/person/:id/name/:name", updateName _)
      )
      .withAutoAcl(true)
  }
}

