package org.dka.tutorial.lagom.timetracker.person.impl

import com.lightbend.lagom.scaladsl.api.ServiceLocator
import com.lightbend.lagom.scaladsl.api.ServiceLocator.NoServiceLocator
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import com.lightbend.lagom.scaladsl.persistence.cassandra.CassandraPersistenceComponents
import com.lightbend.lagom.scaladsl.server.{LagomApplication, LagomApplicationContext, LagomApplicationLoader, LagomServer}
import com.softwaremill.macwire._
import org.dka.tutorial.lagom.timetracker.person.api.PersonService
import play.api.libs.ws.ahc.AhcWSComponents

/**
  * load the [[PersonService]] application
  *
  * this must be enabled in the ```application.conf``` file by setting the ```play.applicaiton.loader```
  */
class PersonServiceLoader extends LagomApplicationLoader {
  override def load(context: LagomApplicationContext): LagomApplication =
    new TimeTrackerApplication(context) {
      override def serviceLocator: ServiceLocator = NoServiceLocator
    }

  override def loadDevMode(context: LagomApplicationContext): LagomApplication =
    new TimeTrackerApplication(context) with LagomDevModeComponents

  override def describeService = Some(readDescriptor[PersonService])
}

abstract class TimeTrackerApplication(context: LagomApplicationContext)
  extends LagomApplication(context)
    //  with CassandraPersistenceComponents
    //  with LagomKafkaComponents
      with AhcWSComponents
{
  /**
    * binds the [[PersonServiceImpl]] to the [[PersonService]] api
    *
    * @return a server
    */
  override lazy val lagomServer: LagomServer = serverFor[PersonService](wire[PersonServiceImpl])

  /**
    * needed by the [[CassandraPersistenceComponents]] to be able to serialize/deserialize to/from cassandra
    */
//  override lazy val jsonSerializerRegistry = PersonSerializerRegistry
}

