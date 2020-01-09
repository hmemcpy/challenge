package challenge

import java.io.IOException

import cats.effect.ExitCode
import challenge.domain.{Event, Stats}
import challenge.http.Api
import io.circe.generic.auto._
import io.circe.parser._
import org.http4s.HttpApp
import org.http4s.implicits._
import org.http4s.server.Router
import org.http4s.server.blaze.BlazeServerBuilder
import zio._
import zio.clock._
import zio.console._
import zio.duration._
import zio.interop.catz._
import zio.interop.catz.implicits._
import zio.stream._

object Main extends CatsApp {
  val eventStream: Stream[IOException, Event] =
    Stream
      .fromInputStream(System.in)
      .chunks
      .transduce(Sink.utf8DecodeChunk)
      .transduce(Sink.splitLines)
      .flatMap(Stream.fromChunk)
      .map(parse _ andThen (_.flatMap(_.as[Event])))
      .collect { case Right(event) => event }

  def consumeEvents(stats: Ref[Stats]): ZIO[Clock, IOException, Unit] =
    eventStream
      .aggregateAsyncWithin(Sink.collectAll[Event], Schedule.spaced(1.second))
      .tap(events => stats.update(_.updated(events)))
      .runDrain

  def runHttpServer[R <: Clock](httpApp: HttpApp[Task], port: Int): ZIO[R, Throwable, Unit] =
    ZIO.environment[R].flatMap { implicit rts =>
      BlazeServerBuilder[Task]
        .bindHttp(port, "0.0.0.0")
        .withHttpApp(httpApp)
        .serve
        .compile[Task, Task, ExitCode]
        .drain
    }

  override def run(args: List[String]): ZIO[ZEnv, Nothing, Int] = {
    val program = for {
      stats <- Ref.make(Stats.empty)

      httpApp = Router[Task](
        "/" -> Api.rootRoute,
        "/events" -> Api.eventRoutes(stats)
      ).orNotFound

      _ <- consumeEvents(stats).fork
      _ <- putStrLn("Listening on http://localhost:8080") *> runHttpServer(httpApp, 8080)
    } yield ()

    program.foldM(err => putStrLn(s"Failed with error: $err").as(1), _ => ZIO.succeed(0))
  }
}
