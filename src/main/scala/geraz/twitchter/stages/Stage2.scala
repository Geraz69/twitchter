package geraz.twitchter.stages

import geraz.twitchter.shapes.{TwitterSource, TwitterSummarySink}
import akka.actor.ActorSystem
import akka.stream.scaladsl.{Broadcast, GraphDSL, RunnableGraph}
import akka.stream.{ActorMaterializer, ClosedShape}
import geraz.twitchter.entities.Tweet

import scala.concurrent.duration._


object Stage2 extends App with TwitterSource with TwitterSummarySink {

  implicit val system: ActorSystem = ActorSystem("twitchter")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  import GraphDSL.Implicits._
  val graph = RunnableGraph.fromGraph(GraphDSL.create() { implicit builder =>
    val A = builder.add(twitterSource)

    val Bcst = builder.add(Broadcast[Tweet](2))

    val B = builder.add(accountSummarySink)
    val C = builder.add(hashTagSummarySink)

    A ~> Bcst ~> B
         Bcst ~> C

    ClosedShape
  })

  graph.run
}
