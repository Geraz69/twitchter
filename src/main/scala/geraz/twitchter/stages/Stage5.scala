package geraz.twitchter.stages

import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, ClosedShape, SourceShape, ThrottleMode}
import akka.stream.scaladsl.{Broadcast, Flow, GraphDSL, Merge, RunnableGraph}

import geraz.twitchter.entities.Tweet
import geraz.twitchter.shapes.{ServerBindingSource, TwitchSummarySink, TwitterSource, TwitterSummarySink}


object Stage5 extends App
  with TwitterSource
  with ServerBindingSource
  with TwitterSummarySink
  with TwitchSummarySink {

  implicit val system: ActorSystem = ActorSystem("twitchter")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  val logTweets = Flow[Tweet].log("something")

  import GraphDSL.Implicits._
  val graph = RunnableGraph.fromGraph(GraphDSL.create() { implicit builder =>
    // Sources
    val A = builder.add(twitterSource)
    val B = builder.add(serverBindingSource)

    // Junctions
    val Merg = builder.add(Merge[Tweet](2))
    val Bcst = builder.add(Broadcast[Tweet](3))

    // Sinks
    val C = builder.add(accountSummarySink)
    val D = builder.add(hashTagSummarySink)
    val E = builder.add(channelSummarySink)

    A  ~> Merg
    B  ~> Merg ~> logTweets ~> Bcst ~> C
                               Bcst ~> D
                               Bcst ~> E

    ClosedShape
  })

  graph.run
}
