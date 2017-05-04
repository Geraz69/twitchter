package geraz.twitchter.stages

import geraz.twitchter.entities._

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Sink
import geraz.twitchter.shapes.TwitterSource

object Stage1 extends App with TwitterSource {

  implicit val system: ActorSystem = ActorSystem("twitchter")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  val printSink = Sink.foreach { tweet: Tweet =>
    println(s"Tweet received: ${tweet}")
  }

  twitterSource.to(printSink).run
}
