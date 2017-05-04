package geraz.twitchter.actors

import akka.actor.Actor
import akka.stream.actor.{ActorSubscriber, OneByOneRequestStrategy}
import geraz.twitchter.entities.Tweet

import scala.collection.mutable.Map

object MostPopularTwitterAccount

class MostPopularTwitterAccount extends Actor with ActorSubscriber {

  private val twitterAccountsMap: Map[String, Int] = Map[String, Int]().withDefaultValue(0)
  private var currentMaxCount: Int = 0
  private var currentMostPopular: Option[String] = None

  override protected val requestStrategy = OneByOneRequestStrategy

  def receive = {
    case t: Tweet =>
      (t.mentions ++ Seq(t.user))
        .filter(_.toLowerCase != "twitch")
        .foreach { account: String =>
          val latest = twitterAccountsMap(account) + 1
          twitterAccountsMap.update(account, latest)
          if (latest >= currentMaxCount) {
            currentMaxCount = latest
            currentMostPopular = Some(account)
          }
        }
    case MostPopularTwitterAccount =>
      println(s"MOST POPULAR TWITTER ACCOUNT: @${currentMostPopular.getOrElse("none")}, => ${currentMaxCount}")
  }
}
