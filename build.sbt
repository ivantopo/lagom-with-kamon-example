organization in ThisBuild := "kamon.example"
version in ThisBuild := "1.0-SNAPSHOT"

// the Scala version that will be used for cross-compiled libraries
scalaVersion in ThisBuild := "2.12.4"

val macwire = "com.softwaremill.macwire" %% "macros" % "2.3.0" % "provided"
val scalaTest = "org.scalatest" %% "scalatest" % "3.0.4" % Test
val kamonDependencies = Seq(
 "io.kamon" %% "kamon-core" % "1.1.0",
  "io.kamon" %% "kamon-play-2.6" % "1.1.0",
  "io.kamon" %% "kamon-scala-future" % "1.0.0",
  "io.kamon" %% "kamon-akka-remote-2.5" % "1.0.0",
  "io.kamon" %% "kamon-executors" % "1.0.0",
  "io.kamon" %% "kamon-jdbc" % "1.0.0",
  "io.kamon" %% "kamon-logback" % "1.0.2",
  "io.kamon" %% "kamon-prometheus" % "1.0.0"
)

lazy val `lagom-with-kamon-example` = (project in file("."))
  .aggregate(`lagom-with-kamon-example-api`, `lagom-with-kamon-example-impl`, `lagom-with-kamon-example-stream-api`, `lagom-with-kamon-example-stream-impl`)

lazy val `lagom-with-kamon-example-api` = (project in file("lagom-with-kamon-example-api"))
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslApi
    )
  )

lazy val `lagom-with-kamon-example-impl` = (project in file("lagom-with-kamon-example-impl"))
  .enablePlugins(LagomScala, JavaAgent)
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslPersistenceCassandra,
      lagomScaladslKafkaBroker,
      lagomScaladslTestKit,
      macwire,
      scalaTest
    ) ++ kamonDependencies,
    dockerExposedPorts := Seq(9000, 9095),
    javaAgents += "org.aspectj" % "aspectjweaver" % "1.9.2",
    javaOptions in Universal += "-Dorg.aspectj.tracing.factory=default"
  )
  .settings(lagomForkedTestSettings: _*)
  .dependsOn(`lagom-with-kamon-example-api`)

lazy val `lagom-with-kamon-example-stream-api` = (project in file("lagom-with-kamon-example-stream-api"))
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslApi
    )
  )

lazy val `lagom-with-kamon-example-stream-impl` = (project in file("lagom-with-kamon-example-stream-impl"))
  .enablePlugins(LagomScala)
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslTestKit,
      macwire,
      scalaTest
    )
  )
  .dependsOn(`lagom-with-kamon-example-stream-api`, `lagom-with-kamon-example-api`)
