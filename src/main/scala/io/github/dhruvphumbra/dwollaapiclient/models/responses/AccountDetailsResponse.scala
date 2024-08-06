package io.github.dhruvphumbra.dwollaapiclient.models.responses

import cats.effect.Concurrent
import io.circe.Decoder
import io.github.dhruvphumbra.dwollaapiclient.models.common.Link
import org.http4s.EntityDecoder
import org.http4s.circe.jsonOf

import java.util.UUID

case class AccountDetailsResponse(_links: Map[String, Link], id: UUID, name: String) derives Decoder

object AccountDetailsResponse:
  given [F[_] : Concurrent]: EntityDecoder[F, AccountDetailsResponse] = jsonOf
