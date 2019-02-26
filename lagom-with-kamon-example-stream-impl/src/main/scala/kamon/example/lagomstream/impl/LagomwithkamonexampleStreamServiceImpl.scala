package kamon.example.lagomstream.impl

import com.lightbend.lagom.scaladsl.api.ServiceCall
import kamon.example.lagomstream.api.LagomwithkamonexampleStreamService
import kamon.example.lagom.api.LagomwithkamonexampleService

import scala.concurrent.Future

/**
  * Implementation of the LagomwithkamonexampleStreamService.
  */
class LagomwithkamonexampleStreamServiceImpl(lagomwithkamonexampleService: LagomwithkamonexampleService) extends LagomwithkamonexampleStreamService {
  def stream = ServiceCall { hellos =>
    Future.successful(hellos.mapAsync(8)(lagomwithkamonexampleService.hello(_).invoke()))
  }
}
