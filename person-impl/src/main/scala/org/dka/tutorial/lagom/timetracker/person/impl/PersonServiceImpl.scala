package org.dka.tutorial.lagom.timetracker.person.impl

import akka.NotUsed
import com.lightbend.lagom.scaladsl.api.ServiceCall
import org.dka.tutorial.lagom.timetracker.person.api.{PersonProfile, PersonService}

import scala.collection.immutable.Seq
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  */
class PersonServiceImpl extends PersonService {
  import PersonServiceImpl._
  override def profile(id: String): ServiceCall[NotUsed, PersonProfile] = ServiceCall { _ => {
    Future {
      if (id == "internal") throw InternalError(id, new Exception("expected"))
      else people.getOrElse(id, throw PersonNotFoundException(id))
    }
  }}
}

object PersonServiceImpl {
  val adam = PersonProfile("1", "Adam", "adam@workplace.org", Some("801-555-1234"))
  val eve = PersonProfile("2", "Eve", "eve@workplace.org", Some("801-555-4321"))
  val unknown = PersonProfile("0", "Unknown", "unknown@workplace.org")
  val people: Map[String, PersonProfile] = Seq(adam, eve).map(p => p.id -> p).toMap
}

