package geraz.twitchter.shapes

import geraz.twitchter.entities._
import akka.actor.ActorSystem
import akka.http.scaladsl.model._
import akka.stream.Materializer
import akka.stream.scaladsl.{Framing, Source}
import akka.util.ByteString
import com.danielasfregola.twitter4s.entities.{Entities, Tweet => FullTweet}
import com.danielasfregola.twitter4s.http.clients.TwitchterClient
import org.json4s.native.Serialization

import scala.concurrent.Await
import scala.concurrent.duration._

trait TwitterSource extends TwitchterClient {

  implicit val system: ActorSystem
  implicit val materializer: Materializer

  val request = HttpRequest(HttpMethods.POST,
    Uri("https://stream.twitter.com/1.1/statuses/filter.json?track=twitch"))

  def twitterSource = {

    implicit val twitchterRequest: HttpRequest = Await.result(withOAuthHeader(materializer)(request), 5 millis)

    Source.single(twitchterRequest)
      .via(connection)

      .flatMapConcat(_.entity.withoutSizeLimit().dataBytes)

      .via(Framing.delimiter(ByteString("\r\n"), Int.MaxValue).async)

      .filter(_.nonEmpty)

      .map { data: ByteString =>
        Serialization.read[FullTweet](data.utf8String.replace('\n', ' '))
      }

      .map { t: FullTweet =>
        val entities: Entities = t.entities.getOrElse(Entities())
        Tweet(t.id, t.user.map(_.screen_name).getOrElse(""), entities.hashtags.map(_.text),
          entities.user_mentions.map(_.screen_name), entities.urls.map(_.expanded_url))
      }
  }
}
