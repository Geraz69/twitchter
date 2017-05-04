package geraz.twitchter.actors

import akka.persistence.{PersistentActor, SnapshotOffer}
import akka.stream.actor.{ActorSubscriber, OneByOneRequestStrategy}
import geraz.twitchter.entities.Tweet

import scala.collection.mutable.Map

object MostPopularTwitchChannel

class MostPopularTwitchChannel extends PersistentActor with ActorSubscriber {
  override def persistenceId = "sample-id-1"

  private var twitchChannelsMap: Map[String, Int] = Map[String, Int]().withDefaultValue(0)
  private var currentMaxCount: Int = 0
  private var currentMostPopular: Option[String] = None
  private var numEntries: Int = 0

  private val snapshotsEvery = 10

  override protected val requestStrategy = OneByOneRequestStrategy

  val receiveCommand: Receive = {
    case t: Tweet =>
      t.urls.map(new java.net.URI(_))
        .filter(_.getHost.contains("twitch.tv"))
        .map(_.getPath.split("/")(1))
        .foreach { channel: String =>
          persist(channel) { _ =>
            val latest = twitchChannelsMap(channel) + 1
            twitchChannelsMap.update(channel, latest)
            if (latest >= currentMaxCount) {
              currentMaxCount = latest
              currentMostPopular = Some(channel)
            }
            if (numEntries % snapshotsEvery == 0) {
              saveSnapshot((twitchChannelsMap, currentMaxCount, currentMostPopular))
            }
          }
        }
    case MostPopularTwitchChannel =>
      println(s"MOST POPULAR TWITCH  CHANNEL: /${currentMostPopular.getOrElse("none")} => ${currentMaxCount}")
  }

  val receiveRecover: Receive = {
    case channel: String =>
      val latest = twitchChannelsMap(channel) + 1
      twitchChannelsMap.update(channel, latest)
      if (latest >= currentMaxCount) {
        currentMaxCount = latest
        currentMostPopular = Some(channel)
      }
    case SnapshotOffer(_, (savedChannelsMap:  Map[String, Int], savedMaxCount: Int, savedMostPopular: Option[String])) =>
      twitchChannelsMap = savedChannelsMap
      currentMaxCount = savedMaxCount
      currentMostPopular = savedMostPopular
  }
}
