package kamon.example.lagomstream.impl

import com.lightbend.lagom.scaladsl.api.ServiceLocator.NoServiceLocator
import com.lightbend.lagom.scaladsl.server._
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import play.api.libs.ws.ahc.AhcWSComponents
import kamon.example.lagomstream.api.LagomwithkamonexampleStreamService
import kamon.example.lagom.api.LagomwithkamonexampleService
import com.softwaremill.macwire._

class LagomwithkamonexampleStreamLoader extends LagomApplicationLoader {

  override def load(context: LagomApplicationContext): LagomApplication =
    new LagomwithkamonexampleStreamApplication(context) {
      override def serviceLocator = NoServiceLocator
    }

  override def loadDevMode(context: LagomApplicationContext): LagomApplication =
    new LagomwithkamonexampleStreamApplication(context) with LagomDevModeComponents

  override def describeService = Some(readDescriptor[LagomwithkamonexampleStreamService])
}

abstract class LagomwithkamonexampleStreamApplication(context: LagomApplicationContext)
  extends LagomApplication(context)
    with AhcWSComponents {

  // Bind the service that this server provides
  override lazy val lagomServer = serverFor[LagomwithkamonexampleStreamService](wire[LagomwithkamonexampleStreamServiceImpl])

  // Bind the LagomwithkamonexampleService client
  lazy val lagomwithkamonexampleService = serviceClient.implement[LagomwithkamonexampleService]
}
