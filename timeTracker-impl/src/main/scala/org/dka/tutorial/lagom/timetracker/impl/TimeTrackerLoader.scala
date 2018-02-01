package org.dka.tutorial.lagom.timetracker.impl

import com.lightbend.lagom.scaladsl.api.ServiceLocator
import com.lightbend.lagom.scaladsl.api.ServiceLocator.NoServiceLocator
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import com.lightbend.lagom.scaladsl.persistence.cassandra.CassandraPersistenceComponents
import com.lightbend.lagom.scaladsl.server.{LagomApplication, LagomApplicationContext, LagomApplicationLoader, LagomServer}
import com.softwaremill.macwire._
import org.dka.tutorial.lagom.timetracker.api.TimeTrackerService
import play.api.libs.ws.ahc.AhcWSComponents

/**
  * load the [[org.dka.tutorial.lagom.timetracker.api.TimeTrackerService]] application
  *
  * this must be enabled in the ```application.conf``` file by setting the ```play.applicaiton.loader```
  */
class TimeTrackerLoader extends LagomApplicationLoader {
  override def load(context: LagomApplicationContext): LagomApplication =
    new TimeTrackerApplication(context) {
      override def serviceLocator: ServiceLocator = NoServiceLocator
    }

  override def loadDevMode(context: LagomApplicationContext): LagomApplication =
    new TimeTrackerApplication(context) with LagomDevModeComponents

  override def describeService = Some(readDescriptor[TimeTrackerService])
}

abstract class TimeTrackerApplication(context: LagomApplicationContext)
  extends LagomApplication(context)
    //  with CassandraPersistenceComponents
    //  with LagomKafkaComponents
      with AhcWSComponents
{
  /**
    * binds the [[TimeTrackerServiceImpl]] to the [[TimeTrackerService]] api
    *
    * @return a server
    */
  override lazy val lagomServer: LagomServer = serverFor[TimeTrackerService](wire[TimeTrackerServiceImpl])

  /**
    * needed by the [[CassandraPersistenceComponents]] to be able to serialize/deserialize to/from cassandra
    */
//  override lazy val jsonSerializerRegistry = TimeTrackerSerializerRegistry
}

