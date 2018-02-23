package org.dka.tutorial.lagom.timetracker.person.impl

import akka.actor.ActorSystem
import akka.testkit.TestKit
import com.lightbend.lagom.scaladsl.playjson.JsonSerializerRegistry
import com.lightbend.lagom.scaladsl.testkit.PersistentEntityTestDriver
import com.lightbend.lagom.scaladsl.testkit.PersistentEntityTestDriver.Reply
import org.dka.tutorial.lagom.timetracker.person.api.PersonData
import org.scalatest.{BeforeAndAfterAll, FunSpec, Matchers}

/**
  * These tests run on the [[Person]] entity
  *
  * These tests are run in order, so the state change in the entity are propagated.
  *
  * The results of these tests are *not* persisted.
  *
  */
class PersonSpec extends FunSpec with Matchers with BeforeAndAfterAll {
  private val system = ActorSystem("PersonSpec", JsonSerializerRegistry.actorSystemSetupFor(PersonSerializerRegistry))
  private val driver = new PersistentEntityTestDriver(system, new Person, "person-1")

  override protected def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  /**
    * execute commands against the person (entity)
    */
  describe("person") {
    val id = "a20afcf5-42aa-44ac-98f1-3e4683334971"
    val initialData = PersonData("testPerson1", "testPerson1@test.org", Some("123-555-1234"))
    val changedName = "changed"
    val changedEmail = "changed@somewhere.org"
    val changedTextNumber = "800-555-4321"
    it("create new person") {
      val outcome = driver.run(Create(id, initialData))
      val events = outcome.events
      events.size shouldBe 1 // PersonCreated
      events.head shouldBe PersonCreated(id, initialData)

      outcome.state shouldBe PersonState(id, initialData.name, initialData.email, initialData.textNumber)

      val sideEffects = outcome.sideEffects
      sideEffects.size shouldBe 1 // the reply, which is the new id
      sideEffects.head.asInstanceOf[Reply].msg shouldBe id

      val issues = outcome.issues
      issues.size shouldBe 0
    }
    it("update name") {
      val expectedState = PersonState(id, changedName, initialData.email, initialData.textNumber)
      val outcome = driver.run(ChangeName(changedName))
      val events = outcome.events
      events.size shouldBe 1 // NameChanged
      events.head shouldBe NameChanged(id, changedName)

      outcome.state shouldBe expectedState

      val sideEffects = outcome.sideEffects
      sideEffects.size shouldBe 1 // the reply, which is the updated person
      sideEffects.head.asInstanceOf[Reply].msg shouldBe PersonState.toProfile(expectedState)

      val issues = outcome.issues
      issues.size shouldBe 0
    }
    it("update email") {
      val expectedState = PersonState(id, changedName, changedEmail, initialData.textNumber)
      val outcome = driver.run(ChangeEmail(changedEmail))
      val events = outcome.events
      events.size shouldBe 1 // EmailChanged
      events.head shouldBe EmailChanged(id, changedEmail)

      outcome.state shouldBe expectedState

      val sideEffects = outcome.sideEffects
      sideEffects.size shouldBe 1 // the reply, which is the updated person
      sideEffects.head.asInstanceOf[Reply].msg shouldBe PersonState.toProfile(expectedState)

      val issues = outcome.issues
      issues.size shouldBe 0
    }
    it("update textNumber") {
      val expectedState = PersonState(id, changedName, changedEmail, Some(changedTextNumber))
      val outcome = driver.run(ChangeTextNumber(Some(changedTextNumber)))
      val events = outcome.events
      events.size shouldBe 1 // TextNumberChanged
      events.head shouldBe TextNumberChanged(id, Some(changedTextNumber))

      outcome.state shouldBe expectedState

      val sideEffects = outcome.sideEffects
      sideEffects.size shouldBe 1 // the reply, which is the updated person
      sideEffects.head.asInstanceOf[Reply].msg shouldBe PersonState.toProfile(expectedState)

      val issues = outcome.issues
      issues.size shouldBe 0
    }
    it("delete textNumber") {
      val expectedState = PersonState(id, changedName, changedEmail, None)
      val outcome = driver.run(ChangeTextNumber(None))
      val events = outcome.events
      events.size shouldBe 1 // TextNumberChanged
      events.head shouldBe TextNumberChanged(id, None)

      outcome.state shouldBe expectedState

      val sideEffects = outcome.sideEffects
      sideEffects.size shouldBe 1 // the reply, which is the updated person
      sideEffects.head.asInstanceOf[Reply].msg shouldBe PersonState.toProfile(expectedState)

      val issues = outcome.issues
      issues.size shouldBe 0
    }
    it("read profile") {
      val expectedState = PersonState(id, changedName, changedEmail, None)
      val outcome = driver.run(Profile)
      val events = outcome.events
      events.size shouldBe 0 // no events, this is a read-only command

      outcome.state shouldBe expectedState

      val sideEffects = outcome.sideEffects
      sideEffects.size shouldBe 1 // the reply, which is the updated person
      sideEffects.head.asInstanceOf[Reply].msg shouldBe PersonState.toProfile(expectedState)

      val issues = outcome.issues
      issues.size shouldBe 0
    }
  }
}
