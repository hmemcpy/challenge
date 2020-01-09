package challenge

import cats.Monoid
import cats.implicits._

package object domain {
  case class Event(event_type: String, data: String, timestamp: Option[Int]) {
    def toStats: Stats = {
      val eventCount = Map(event_type -> 1)
      val uniqueWords = data
        .split(' ')
        .map(_.trim)
        .groupMapReduce(identity)(_ => 1)(_ + _)
      Stats(eventCount, uniqueWords)    }
  }

  case class Stats(eventCount: Map[String, Int], uniqueWords: Map[String, Int]) {
    def updated(events: List[Event]): Stats =
      this |+| events.foldMap(_.toStats)
  }

  object Stats {
    val empty: Stats = Stats(Map.empty, Map.empty)

    implicit val StatsMonoid: Monoid[Stats] = new Monoid[Stats] {
      override def empty: Stats = Stats.empty

      override def combine(x: Stats, y: Stats): Stats =
        Stats(x.eventCount |+| y.eventCount, x.uniqueWords |+| y.uniqueWords)
    }
  }
}
