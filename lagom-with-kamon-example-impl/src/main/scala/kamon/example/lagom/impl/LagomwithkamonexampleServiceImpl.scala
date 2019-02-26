package kamon.example.lagom.impl

import kamon.example.lagom.api
import kamon.example.lagom.api.{LagomwithkamonexampleService}
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.api.broker.Topic
import com.lightbend.lagom.scaladsl.broker.TopicProducer
import com.lightbend.lagom.scaladsl.persistence.{EventStreamElement, PersistentEntityRegistry}

/**
  * Implementation of the LagomwithkamonexampleService.
  */
class LagomwithkamonexampleServiceImpl(persistentEntityRegistry: PersistentEntityRegistry) extends LagomwithkamonexampleService {

  override def hello(id: String) = ServiceCall { _ =>
    // Look up the lagom-with-kamon-example entity for the given ID.
    val ref = persistentEntityRegistry.refFor[LagomwithkamonexampleEntity](id)

    // Ask the entity the Hello command.
    ref.ask(Hello(id))
  }

  override def useGreeting(id: String) = ServiceCall { request =>
    // Look up the lagom-with-kamon-example entity for the given ID.
    val ref = persistentEntityRegistry.refFor[LagomwithkamonexampleEntity](id)

    // Tell the entity to use the greeting message specified.
    ref.ask(UseGreetingMessage(request.message))
  }


  override def greetingsTopic(): Topic[api.GreetingMessageChanged] =
    TopicProducer.singleStreamWithOffset {
      fromOffset =>
        persistentEntityRegistry.eventStream(LagomwithkamonexampleEvent.Tag, fromOffset)
          .map(ev => (convertEvent(ev), ev.offset))
    }

  private def convertEvent(helloEvent: EventStreamElement[LagomwithkamonexampleEvent]): api.GreetingMessageChanged = {
    helloEvent.event match {
      case GreetingMessageChanged(msg) => api.GreetingMessageChanged(helloEvent.entityId, msg)
    }
  }
}
