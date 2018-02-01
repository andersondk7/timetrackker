package org.dka.tutorial.lagom.timetracker.api

import akka.NotUsed
import com.lightbend.lagom.scaladsl.api.{Descriptor, Service, ServiceCall}

/**
  *
  */
trait TimeTrackerService extends Service {

  def profile(id: String): ServiceCall[NotUsed, PersonProfile]

  override final def descriptor: Descriptor = {
    import Service._
    named("timeTracker")
      .withCalls(
        pathCall("/timetracker/profile/:id", profile _)
      )
      .withAutoAcl(true)
  }
}

