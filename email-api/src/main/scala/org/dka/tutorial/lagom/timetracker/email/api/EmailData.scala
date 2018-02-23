package org.dka.tutorial.lagom.timetracker.email.api

import play.api.libs.json.{Format, Json}

/**
  * meta data for a email
  */
case class EmailData(template: String, data: Map[String, String]) { }

object EmailData {
  implicit val format: Format[EmailData] = Json.format[EmailData]
}
