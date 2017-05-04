package geraz.twitchter.stages

import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, ClosedShape}
import akka.stream.scaladsl.{Broadcast, GraphDSL, RunnableGraph, Sink}
import geraz.twitchter.entities.Tweet
import geraz.twitchter.shapes.{TwitchSummarySink, TwitterSource, TwitterSummarySink}


object Stage3 extends App with TwitterSource with TwitterSummarySink with TwitchSummarySink {

  implicit val system: ActorSystem = ActorSystem("twitchter")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  import GraphDSL.Implicits._
  val graph = RunnableGraph.fromGraph(GraphDSL.create() { implicit builder =>
    val A = builder.add(twitterSource)

    val Bcst = builder.add(Broadcast[Tweet](3))

    val B = builder.add(accountSummarySink)
    val C = builder.add(hashTagSummarySink)
    val D = builder.add(channelSummarySink)

         Bcst ~> B
    A ~> Bcst ~> C
         Bcst ~> D

    ClosedShape
  })

  graph.run
}
