package geraz.twitchter.stages

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Sink
import geraz.twitchter.entities.Tweet
import geraz.twitchter.shapes.ServerBindingSource


object Stage4 extends App with ServerBindingSource {

  implicit val system: ActorSystem = ActorSystem("twitchter")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  val printSink = Sink.foreach { tweet: Tweet =>
    println(s"Tweet received: ${tweet}")
  }

  serverBindingSource.runWith(printSink)
}
