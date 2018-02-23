package org.dka.tutorial.lagom.timetracker.email.impl

import java.time.LocalDateTime

import akka.event.slf4j.Logger

/**
  */
trait EmailDeliveryService {
  def send(address: String, template: String, data: Map[String, String]): LocalDateTime
}

class EmailDeliveryServiceImpl extends EmailDeliveryService {
  private val logger = Logger.apply(this.getClass.getName)

  override def send(address: String, template: String, data: Map[String, String]): LocalDateTime = {
    logger.info(s"sent $template to $address")
    LocalDateTime.now()
  }
}
