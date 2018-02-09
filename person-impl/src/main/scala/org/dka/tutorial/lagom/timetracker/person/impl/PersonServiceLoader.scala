package org.dka.tutorial.lagom.timetracker.person.impl

import com.lightbend.lagom.scaladsl.api.ServiceLocator
import com.lightbend.lagom.scaladsl.api.ServiceLocator.NoServiceLocator
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import com.lightbend.lagom.scaladsl.persistence.cassandra.{CassandraPersistenceComponents, WriteSideCassandraPersistenceComponents}
import com.lightbend.lagom.scaladsl.persistence.jdbc.ReadSideJdbcPersistenceComponents
import com.lightbend.lagom.scaladsl.playjson.JsonSerializerRegistry
import com.lightbend.lagom.scaladsl.server.{LagomApplication, LagomApplicationContext, LagomApplicationLoader, LagomServer}
import com.softwaremill.macwire._
import org.dka.tutorial.lagom.timetracker.person.api.PersonService
import play.api.db.{ConnectionPool, HikariCPConnectionPool}
import play.api.libs.ws.ahc.AhcWSComponents


/**
  * load the [[PersonApplication]] application
  *
  * this must be enabled in the ```application.conf``` file by setting the ```play.application.loader```
  */
class PersonServiceLoader extends LagomApplicationLoader {
  override def load(context: LagomApplicationContext): LagomApplication =
    new PersonApplication(context) {
      override def serviceLocator: ServiceLocator = NoServiceLocator
    }

  override def loadDevMode(context: LagomApplicationContext): LagomApplication =
    new PersonApplication(context) with LagomDevModeComponents

  override def describeService = Some(readDescriptor[PersonService])
}

/**
  * represents the web service application
  */
abstract class PersonApplication(context: LagomApplicationContext)
  extends LagomApplication(context)
    with WriteSideCassandraPersistenceComponents // commands/events are written to Cassandra
    with ReadSideJdbcPersistenceComponents // query side tables are in Postgres
    //  with LagomKafkaComponents
    with AhcWSComponents {

  override def connectionPool: ConnectionPool = wire[HikariCPConnectionPool] // read from the application.conf (thanks to play)

  /**
    * binds the [[PersonServiceImpl]] to the [[PersonService]] api
    *
    * @return the server
    */
  override lazy val lagomServer: LagomServer = serverFor[PersonService](wire[PersonServiceImpl])

  /**
    * needed by the [[CassandraPersistenceComponents]] to be able to serialize/deserialize commands/events to/from cassandra
    */
  override lazy val jsonSerializerRegistry: JsonSerializerRegistry = PersonSerializerRegistry

  /**
    * create lookups for entity instances
    *
    * see [[PersonServiceImpl]] for example of looking up an entity
    */
  persistentEntityRegistry.register(wire[Person])
}

