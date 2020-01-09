package challenge.http

import challenge.domain.Stats
import io.circe.Encoder
import org.http4s.circe.jsonEncoderOf
import org.http4s.dsl.Http4sDsl
import org.http4s.headers.`Content-Type`
import org.http4s.{EntityEncoder, HttpRoutes, MediaType}
import zio.interop.catz._
import zio.{Ref, Task}

object Api {
  val dsl: Http4sDsl[Task] = Http4sDsl[Task]
  import dsl._

  def rootRoute: HttpRoutes[Task] =
    HttpRoutes.of[Task] {
      case GET -> Root =>
        Ok(
          """<h2>Select your API:</h2>
            |<ul>
            |<li><a href=/events/countByEventType>countByEventType</a></li>
            |<li><a href=/events/countWords>countWords</a></li>
            |</ul>""".stripMargin,
          `Content-Type`(MediaType.text.html)
        )
    }

  def eventRoutes(stats: Ref[Stats]): HttpRoutes[Task] = {
    implicit def encoder[A](implicit encoder: Encoder[A]): EntityEncoder[Task, A] = jsonEncoderOf[Task, A]

    HttpRoutes.of[Task] {
      case GET -> Root / "countByEventType" =>
        stats.get.flatMap(s => Ok(s.eventCount))
      case GET -> Root / "countWords" =>
        stats.get.flatMap(s => Ok(s.uniqueWords))
    }
  }
}
