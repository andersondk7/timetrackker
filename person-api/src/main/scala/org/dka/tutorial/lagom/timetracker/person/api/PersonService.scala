package org.dka.tutorial.lagom.timetracker.person.api

import akka.NotUsed
import com.lightbend.lagom.scaladsl.api.transport.Method
import com.lightbend.lagom.scaladsl.api.{Descriptor, Service, ServiceCall}

/**
  *
  */
trait PersonService extends Service {

  override final def descriptor: Descriptor = {
    import Service._
    named("person")
      .withCalls(
        pathCall("/person/profile/:id", profile _),
        restCall(Method.POST, "/person", create _),
        restCall(Method.PUT, "/person/:id/name/:name", updateName _),
        restCall(Method.PUT, "/person/:id/email/:email", updateEmail _),
        restCall(Method.PUT, "/person/:id/textNumber/:textNumber", updateTextNumber _),
        restCall(Method.DELETE, "/person/:id/textNumber", deleteTextNumber _)
      )
      .withAutoAcl(true)
  }

  def profile(id: String): ServiceCall[NotUsed, PersonProfile]

  def create(): ServiceCall[PersonData, String]

  def updateName(id: String, name: String): ServiceCall[NotUsed, PersonProfile]

  def updateEmail(id: String, email: String): ServiceCall[NotUsed, PersonProfile]

  def updateTextNumber(id: String, textNumber: String): ServiceCall[NotUsed, PersonProfile]

  def deleteTextNumber(id: String): ServiceCall[NotUsed, PersonProfile]
}

