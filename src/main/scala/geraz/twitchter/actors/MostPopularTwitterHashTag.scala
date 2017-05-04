package geraz.twitchter.actors

import akka.actor.Actor
import akka.stream.actor.{ActorSubscriber, OneByOneRequestStrategy}
import geraz.twitchter.entities.Tweet

import scala.collection.mutable.Map

object MostPopularTwitterHashTag

class MostPopularTwitterHashTag extends Actor with ActorSubscriber {

  private val twitterHashTagsMap: Map[String, Int] = Map[String, Int]().withDefaultValue(0)
  private var currentMaxCount: Int = 0
  private var currentMostPopular: Option[String] = None

  override protected val requestStrategy = OneByOneRequestStrategy

  def receive = {
    case t: Tweet =>
      t.hashTags
        .filter(_.toLowerCase != "twitch")
        .foreach { hashTag: String =>
          val latest = twitterHashTagsMap(hashTag) + 1
          twitterHashTagsMap.update(hashTag, latest)
          if (latest >= currentMaxCount) {
            currentMaxCount = latest
            currentMostPopular = Some(hashTag)
          }
        }
    case MostPopularTwitterHashTag =>
      println(s"MOST POPULAR TWITTER HASHTAG: #${currentMostPopular.getOrElse("none")}, => ${currentMaxCount}")
  }
}
