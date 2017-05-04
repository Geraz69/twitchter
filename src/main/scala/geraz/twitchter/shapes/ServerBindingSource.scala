package geraz.twitchter.shapes

import akka.actor.ActorSystem
import akka.stream.{Materializer, SourceShape, ThrottleMode}
import akka.stream.scaladsl.{Broadcast, Flow, Framing, GraphDSL, Tcp}
import akka.util.ByteString
import com.danielasfregola.twitter4s.TwitterRestClient
import com.danielasfregola.twitter4s.entities.{Entities, RatedData, Tweet => FullTweet}
import geraz.twitchter.entities.Tweet

import scala.concurrent.duration._

trait ServerBindingSource {

  implicit val system: ActorSystem
  implicit val materializer: Materializer

  val client = TwitterRestClient()

  val reqToIdsFlow = Flow[ByteString]
    .via(Framing.delimiter(ByteString("\n"), maximumFrameLength = 256, allowTruncation = true))
    .map(_.utf8String.trim)
    .filter(_.matches("\\d+"))
    .map(_.toLong)

  val throttleIdsFlow = Flow[Long].throttle(1, 1 second, 1, ThrottleMode.shaping)

  val idsToTweetsFlow = Flow[Long]
    .mapAsync(1) { x =>
      import scala.concurrent.ExecutionContext
      import ExecutionContext.Implicits._
      client.getTweet(x).map(Some(_)).recover { case _ => None}
    }
    .collect { case Some(r: RatedData[FullTweet]) =>
      val t = r.data.asInstanceOf[FullTweet]
      val entities: Entities = t.entities.getOrElse(Entities())
      Tweet(t.id, t.user.map(_.screen_name).getOrElse(""), entities.hashtags.map(_.text),
        entities.user_mentions.map(_.screen_name), entities.urls.map(_.expanded_url))
    }

  val tweetsToResFlow = Flow[Tweet]
    .map(t => t.id + " -> " + t.user + "\n")
    .map(ByteString(_))

  def serverBindingSource = {

    Tcp().bind(interface = "127.0.0.1", port = 8090)
      .flatMapConcat { connection =>
        println(s"New connection from: ${connection.remoteAddress}")

        import GraphDSL.Implicits._
        GraphDSL.create() { implicit builder =>
          val Bcast = builder.add(Broadcast[Tweet](2))
          val Conn = builder.add(connection.flow)

          Conn  ~> reqToIdsFlow ~> throttleIdsFlow ~> idsToTweetsFlow ~> Bcast // ~> someSink
          Conn  <~                 tweetsToResFlow                    <~ Bcast

          SourceShape(Bcast.out(1))
        }
      }
  }

}