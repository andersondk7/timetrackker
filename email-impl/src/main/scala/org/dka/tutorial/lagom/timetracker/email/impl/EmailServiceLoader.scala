package org.dka.tutorial.lagom.timetracker.email.impl

import com.lightbend.lagom.scaladsl.api.ServiceLocator
import com.lightbend.lagom.scaladsl.api.ServiceLocator.NoServiceLocator
import com.lightbend.lagom.scaladsl.broker.kafka.LagomKafkaComponents
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import com.lightbend.lagom.scaladsl.persistence.cassandra.{CassandraPersistenceComponents, WriteSideCassandraPersistenceComponents}
import com.lightbend.lagom.scaladsl.persistence.jdbc.ReadSideJdbcPersistenceComponents
import com.lightbend.lagom.scaladsl.playjson.JsonSerializerRegistry
import com.lightbend.lagom.scaladsl.server.{LagomApplication, LagomApplicationContext, LagomApplicationLoader, LagomServer}
import com.softwaremill.macwire._
import org.dka.tutorial.lagom.timetracker.email.api.EmailService
import org.dka.tutorial.lagom.timetracker.person.api.PersonService
import play.api.db.{ConnectionPool, HikariCPConnectionPool}
import play.api.libs.ws.ahc.AhcWSComponents


/**
  * load the [[EmailApplication]] application
  *
  * this must be enabled in the ```application.conf``` file by setting the ```play.application.loader```
  */
class EmailServiceLoader extends LagomApplicationLoader {
  override def load(context: LagomApplicationContext): LagomApplication =
    new EmailApplication(context) {
      override def serviceLocator: ServiceLocator = NoServiceLocator
    }

  override def loadDevMode(context: LagomApplicationContext): LagomApplication =
    new EmailApplication(context) with LagomDevModeComponents

  override def describeService = Some(readDescriptor[EmailService])
}

/**
  * represents the web service application
  */
abstract class EmailApplication(context: LagomApplicationContext)
  extends LagomApplication(context)
    with WriteSideCassandraPersistenceComponents // commands/events are written to Cassandra
    with ReadSideJdbcPersistenceComponents // query side tables are in Postgres
    with LagomKafkaComponents  // provides asynchronous event message broker between services
    with AhcWSComponents {

  //  context.playContext.initialConfiguration.get[String]("path")
  lazy val emailDeliveryService = wire[EmailDeliveryServiceImpl]
  lazy val personService = serviceClient.implement[PersonService]

  /**
    * binds the [[EmailServiceImpl]] to the [[EmailService]] api
    *
    * @return the server
    */
  override lazy val lagomServer: LagomServer = serverFor[EmailService](wire[EmailServiceImpl])
  /**
    * needed by the [[CassandraPersistenceComponents]] to be able to serialize/deserialize commands/events to/from cassandra
    */
  override lazy val jsonSerializerRegistry: JsonSerializerRegistry = EmailSerializerRegistry

  override def connectionPool: ConnectionPool = wire[HikariCPConnectionPool] // read from the application.conf (thanks to play)

  /**
    * create lookups for entity instances
    *
    * see [[EmailServiceImpl]] for example of looking up an entity
    */
  persistentEntityRegistry.register(wire[Recipient])
}
