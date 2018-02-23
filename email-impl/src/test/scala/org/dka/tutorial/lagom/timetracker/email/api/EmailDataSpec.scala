package org.dka.tutorial.lagom.timetracker.email.api

import org.scalatest.{FunSpec, Matchers}
import play.api.libs.json.Json

/**
  */
class EmailDataSpec extends FunSpec with Matchers {
  describe("Json") {
    val emailData = EmailData("template", Map("key1" -> "value1", "key2" -> "value2"))
    it("should write to json") {
      val json = Json.toJson(emailData)
      println(s"json:\n ${Json.prettyPrint(json)}\n")

    }
  }

}
