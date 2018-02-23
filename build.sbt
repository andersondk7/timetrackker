organization in ThisBuild := "org.dka.tutorials.lagom"
//organization in ThisBuild := "com.example"
version in ThisBuild := "1.0-SNAPSHOT"

// the Scala version that will be used for cross-compiled libraries
scalaVersion in ThisBuild := "2.12.4"

val macwire = "com.softwaremill.macwire" %% "macros" % "2.3.0" % "provided"
val scalaTest = "org.scalatest" %% "scalatest" % "3.0.4" % Test
val postgres= "org.postgresql" % "postgresql" % "9.4.1212"

lazy val `timeTracker-root` = (project in file("."))
  .aggregate(`person-api`, `person-impl`, `email-api`, `email-impl`)

lazy val `person-api` = (project in file("person-api"))
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslApi
    )
  )

lazy val `person-impl` = (project in file("person-impl"))
  .enablePlugins(LagomScala)
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslPersistenceCassandra,
      lagomScaladslPersistenceJdbc,
      lagomScaladslKafkaBroker,
      lagomScaladslTestKit,
      postgres,
      macwire,
      scalaTest
    )
  )
  .settings(lagomForkedTestSettings: _*)
  .dependsOn(`person-api`)


lazy val `email-api` = (project in file("email-api"))
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslApi
    )
  )

lazy val `email-impl` = (project in file("email-impl"))
  .enablePlugins(LagomScala)
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslPersistenceCassandra,
      lagomScaladslPersistenceJdbc,
      lagomScaladslKafkaBroker,
      lagomScaladslTestKit,
      postgres,
      macwire,
      scalaTest
    )
  )
  .settings(lagomForkedTestSettings: _*)
  .dependsOn(`email-api`, `person-api`)

lagomCassandraCleanOnStart in ThisBuild := true
//lagomCassandraCleanOnStart in ThisBuild := false // so we don't have to keep creating instances...
