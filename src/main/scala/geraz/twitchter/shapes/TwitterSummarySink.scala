package geraz.twitchter.shapes

import geraz.twitchter.actors.{MostPopularTwitchChannel, MostPopularTwitterAccount, MostPopularTwitterHashTag}
import geraz.twitchter.entities.Tweet
import akka.actor.{ActorSystem, PoisonPill, Props}
import akka.stream.Materializer
import akka.stream.scaladsl.{Broadcast, Sink}

import scala.concurrent.duration._

trait TwitterSummarySink {

  implicit val system: ActorSystem
  implicit val materializer: Materializer

  import system.dispatcher

  def hashTagSummarySink = {
    val mostPopularTwitterHashTagActor = system.actorOf(Props[MostPopularTwitterHashTag])

    system.scheduler.schedule(5 seconds, 5 seconds, mostPopularTwitterHashTagActor, MostPopularTwitterHashTag)

    Sink.actorRef(mostPopularTwitterHashTagActor, PoisonPill)
  }

  def accountSummarySink = {
    val mostPopularTwitterAccountActor = system.actorOf(Props[MostPopularTwitterAccount])

    system.scheduler.schedule(5 seconds, 5 seconds, mostPopularTwitterAccountActor, MostPopularTwitterAccount)

    Sink.actorRef(mostPopularTwitterAccountActor, PoisonPill)
  }

}
