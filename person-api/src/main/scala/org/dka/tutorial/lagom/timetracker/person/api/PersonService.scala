package org.dka.tutorial.lagom.timetracker.person.api

import akka.NotUsed
import com.lightbend.lagom.scaladsl.api.transport.Method
import com.lightbend.lagom.scaladsl.api.{Descriptor, Service, ServiceCall}

/**
  * defines management of person ([[PersonData]],[[PersonProfile]])
  */
trait PersonService extends Service {
  import Service._

  /**
    * defines the methods in the service
    *
    * map each endpoint with an implementing function
    */
  override final def descriptor: Descriptor =
    named("person")
      .withCalls(
        pathCall("/person/profile/:id", profile _),
        restCall(Method.POST, "/person", create _),
        restCall(Method.PUT, "/person/:id/name/:name", updateName _),
        restCall(Method.PUT, "/person/:id/email/:email", updateEmail _),
        restCall(Method.PUT, "/person/:id/textNumber/:textNumber", updateTextNumber _),
        restCall(Method.DELETE, "/person/:id/textNumber", deleteTextNumber _),
        restCall(Method.GET, "/profiles", profileSummaries)
      )
      .withAutoAcl(true)

  // ---------------------------------------------------------
  // implementation functions
  // ---------------------------------------------------------

  /**
    * @return [[PersonProfile]] for given id
    */
  def profile(id: String): ServiceCall[NotUsed, PersonProfile]

  /**
    * creates a new person from [[PersonData]] as json in the request body
    * @return new person's id
    */
  def create(): ServiceCall[PersonData, String]

  /**
    * change the name of a person
    * @param id unique identifier of the person for whom the name will be changed
    * @param name new name for the person
    * @return updated profile of the person
    */
  def updateName(id: String, name: String): ServiceCall[NotUsed, PersonProfile]

  /**
    * change the email of a person
    * @param id unique identifier of the person for whom the name will be changed
    * @param email new email address of person
    * @return updated profile of the person
    */
  def updateEmail(id: String, email: String): ServiceCall[NotUsed, PersonProfile]

  /**
    * change the text number of a person
    *
    * to remove the text number use [[deleteTextNumber]]
    * @param id unique identifier of the person for whom the name will be changed
    * @param textNumber new text number
    * @return updated profile of the person
    */
  def updateTextNumber(id: String, textNumber: String): ServiceCall[NotUsed, PersonProfile]

  /**
    * delete the text number of a person
    * @param id unique identifier of the person for whom the name will be changed
    * @return updated profile of the person
    */
  def deleteTextNumber(id: String): ServiceCall[NotUsed, PersonProfile]

  /**
    * @return [[PersonProfile]] for all persons
    */
  def profileSummaries: ServiceCall[NotUsed, Seq[PersonProfile]]
}

