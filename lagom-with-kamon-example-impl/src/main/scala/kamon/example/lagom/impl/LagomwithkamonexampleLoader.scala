package kamon.example.lagom.impl

import com.lightbend.lagom.scaladsl.api.ServiceLocator
import com.lightbend.lagom.scaladsl.api.ServiceLocator.NoServiceLocator
import com.lightbend.lagom.scaladsl.persistence.cassandra.CassandraPersistenceComponents
import com.lightbend.lagom.scaladsl.server._
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import play.api.libs.ws.ahc.AhcWSComponents
import kamon.example.lagom.api.LagomwithkamonexampleService
import com.lightbend.lagom.scaladsl.broker.kafka.LagomKafkaComponents
import com.softwaremill.macwire._
import kamon.Kamon
import kamon.prometheus.PrometheusReporter

import scala.concurrent.Future

class LagomwithkamonexampleLoader extends LagomApplicationLoader {

  override def load(context: LagomApplicationContext): LagomApplication =
    new LagomwithkamonexampleApplication(context) {
      override def serviceLocator: ServiceLocator = NoServiceLocator
    }

  override def loadDevMode(context: LagomApplicationContext): LagomApplication =
    new LagomwithkamonexampleApplication(context) with LagomDevModeComponents

  override def describeService = Some(readDescriptor[LagomwithkamonexampleService])
}

abstract class LagomwithkamonexampleApplication(context: LagomApplicationContext)
  extends LagomApplication(context)
    with CassandraPersistenceComponents
    with LagomKafkaComponents
    with AhcWSComponents {

  // These lines are only necessary when doing compile-time dependency injection. When using Guice, a GuiceModule that
  // is included in kamon-play should be automatically picked up and configured.
  Kamon.reconfigure(context.playContext.initialConfiguration.underlying)
  Kamon.addReporter(new PrometheusReporter())

  context.playContext.lifecycle.addStopHook{ () ⇒
    Future.successful(Kamon.stopAllReporters())
  }

  // Bind the service that this server provides
  override lazy val lagomServer = serverFor[LagomwithkamonexampleService](wire[LagomwithkamonexampleServiceImpl])

  // Register the JSON serializer registry
  override lazy val jsonSerializerRegistry = LagomwithkamonexampleSerializerRegistry

  // Register the lagom-with-kamon-example persistent entity
  persistentEntityRegistry.register(wire[LagomwithkamonexampleEntity])
}
