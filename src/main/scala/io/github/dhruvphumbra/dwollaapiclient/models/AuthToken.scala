package io.github.dhruvphumbra.dwollaapiclient.models

import cats.effect.Concurrent
import io.circe.derivation.Configuration
import io.circe.literal.json
import io.circe.{Decoder, Encoder, Json}
import org.http4s.circe.*
import org.http4s.*

case class AuthToken(access_token: String) derives Decoder

object AuthToken:
  given[F[_] : Concurrent]: EntityDecoder[F, AuthToken] = jsonOf
