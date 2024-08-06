package io.github.dhruvphumbra.dwollaapiclient.models.responses

import cats.effect.Concurrent
import io.circe.Decoder.Result
import io.circe.{Decoder, HCursor, Json}
import io.github.dhruvphumbra.dwollaapiclient.models.common.*
import org.http4s.EntityDecoder
import org.http4s.circe.jsonOf

import java.time.ZonedDateTime
import java.util.UUID

case class ListFundingSourceResponse(
                                      _links: Map[String, Link],
                                      fundingSources: List[FundingSourceResponse]
                                    )

object ListFundingSourceResponse:
  given Decoder[ListFundingSourceResponse] = new Decoder[ListFundingSourceResponse]:
    override def apply(c: HCursor): Result[ListFundingSourceResponse] =
      for {
        _links <- c.get[Map[String, Link]]("_links")
        fundingSources <- c.downField("_embedded").get[List[FundingSourceResponse]]("funding-sources")
      } yield ListFundingSourceResponse(_links, fundingSources)

  given [F[_] : Concurrent]: EntityDecoder[F, ListFundingSourceResponse] = jsonOf
