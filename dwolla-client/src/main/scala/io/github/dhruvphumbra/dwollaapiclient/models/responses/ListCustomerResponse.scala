package io.github.dhruvphumbra.dwollaapiclient.models.responses

import cats.effect.Concurrent
import io.circe.Decoder.Result
import io.circe.{Decoder, HCursor, Json}
import io.github.dhruvphumbra.dwollaapiclient.models.common.*
import org.http4s.EntityDecoder
import org.http4s.circe.jsonOf

import java.time.ZonedDateTime
import java.util.UUID

case class ListCustomerResponse(
                                 _links: Map[String, Link],
                                 customers: List[Customer],
                                 total: Long
                               )

object ListCustomerResponse:
  given Decoder[ListCustomerResponse] = new Decoder[ListCustomerResponse]:
    override def apply(c: HCursor): Result[ListCustomerResponse] =
      for {
        _links <- c.get[Map[String, Link]]("_links")
        customers <- c.downField("_embedded").get[List[Customer]]("customers")
        total <- c.get[Long]("total")
      } yield ListCustomerResponse(_links, customers, total)

  given [F[_] : Concurrent]: EntityDecoder[F, ListCustomerResponse] = jsonOf
