package geraz.twitchter.stages

import java.net.InetSocketAddress

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source, Tcp}
import akka.util.ByteString

object Sender extends App {
  implicit val system: ActorSystem = ActorSystem("twitchter")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  val fileName = args(0)

  val serverConn = Tcp().outgoingConnection(new InetSocketAddress("127.0.0.1", 8090))

  val getLines = () => {
    scala.io.Source.fromFile(fileName).getLines
  }

  val linesSource = Source.fromIterator(getLines)
    .map { line: String  =>
      ByteString(line + "\n")
    }

  linesSource.via(serverConn).runForeach { response =>
    println(response.utf8String.trim)
  }
}
