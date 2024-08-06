package io.github.dhruvphumbra.dwollaapiclient.models.responses

import cats.effect.Concurrent
import io.circe.Decoder.Result
import io.circe.{Decoder, HCursor}
import io.github.dhruvphumbra.dwollaapiclient.models.common.*
import io.github.dhruvphumbra.dwollaapiclient.models.requests.FundingSourceType
import org.http4s.EntityDecoder
import org.http4s.circe.jsonOf

import java.time.ZonedDateTime
import java.util.UUID

final case class FundingSourceResponse(
                                        _links: Map[String, Link],
                                        id: UUID,
                                        status: String,
                                        `type`: String,
                                        bankAccountType: FundingSourceType,
                                        name: String,
                                        created: ZonedDateTime,
                                        removed: Boolean,
                                        channels: List[String],
                                        bankName: Option[String],
                                        fingerprint: String
                                      )derives Decoder

object FundingSourceResponse:
  given [F[_] : Concurrent]: EntityDecoder[F, FundingSourceResponse] = jsonOf

