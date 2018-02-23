package org.dka.tutorial.lagom.timetracker.person.impl

import com.lightbend.lagom.scaladsl.server.LocalServiceLocator
import com.lightbend.lagom.scaladsl.testkit.ServiceTest
import org.dka.tutorial.lagom.timetracker.person.api.{PersonData, PersonProfile, PersonService}
import org.scalatest.{AsyncWordSpec, BeforeAndAfterAll, Matchers}

/**
  */
class PersonServiceSpec extends AsyncWordSpec with Matchers with BeforeAndAfterAll {
  private val server = ServiceTest.startServer(
    ServiceTest.defaultSetup
      .withCassandra()
  ) { ctx => new PersonApplication(ctx) with LocalServiceLocator }

  private val client = server.serviceClient.implement[PersonService]

  override protected def afterAll(): Unit = server.stop()

  "personService" should {
    "manipulate a person's data" in {
      val name = "finalName"
      val email = "finalName@somewhere.org"
      val textNumber = "800-555-1234"
      val test = for {
        id <- client.create().invoke(PersonData("testPerson", "testPerson@somewhere.org"))
        _ <- client.updateName(id, name).invoke()
        _ <- client.updateEmail(id, email).invoke()
        _ <- client.updateTextNumber(id, textNumber).invoke()
        withTextNumber <- client.profile(id).invoke()
        _ <- client.deleteTextNumber(id).invoke
        withoutTextNumber <- client.profile(id).invoke()
      } yield (id, withTextNumber, withoutTextNumber)
      test.map { results =>
        val (id, withTextNumber, withoutTextNumber) = results
        withTextNumber shouldBe PersonProfile(id, name, email, Some(textNumber))
        withoutTextNumber shouldBe PersonProfile(id, name, email)
      }
    }
  }

}
