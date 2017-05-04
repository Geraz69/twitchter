package geraz.twitchter.shapes

import akka.actor.{ActorSystem, PoisonPill, Props}
import akka.stream.Materializer
import akka.stream.scaladsl.{Broadcast, Sink}
import geraz.twitchter.actors.{MostPopularTwitchChannel, MostPopularTwitterAccount, MostPopularTwitterHashTag}
import geraz.twitchter.entities.Tweet

import scala.concurrent.duration._

trait TwitchSummarySink {

  implicit val system: ActorSystem
  implicit val materializer: Materializer

  def channelSummarySink = {
    val mostPopularTwitchChannelActor = system.actorOf(Props[MostPopularTwitchChannel])

    import system.dispatcher
    system.scheduler.schedule(5 seconds, 5 seconds, mostPopularTwitchChannelActor, MostPopularTwitchChannel)

    Sink.actorRef(mostPopularTwitchChannelActor, PoisonPill)
  }

}
