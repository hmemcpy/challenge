package challenge

import java.io.IOException

import challenge.domain.{Event, Stats}
import io.circe.generic.auto._
import io.circe.parser._
import zio._
import zio.clock._
import zio.console._
import zio.duration._
import zio.stream._

object Main extends App {
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

  override def run(args: List[String]): ZIO[ZEnv, Nothing, Int] =
    for {
      stats <- Ref.make(Stats.empty)
      _     <- consumeEvents(stats).fork
      _     <- stats.get.flatMap(s => putStrLn(s.toString)).repeat(Schedule.fixed(5.seconds))
    } yield 0
}
