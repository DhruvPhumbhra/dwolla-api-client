package io.github.dhruvphumbra.dwollaapiclient.models

import cats.effect.Concurrent
import io.circe.Decoder
import io.circe.derivation.Configuration
import org.http4s.*
import org.http4s.circe.*

case class AuthToken(access_token: String) derives Decoder

object AuthToken:
  given[F[_] : Concurrent]: EntityDecoder[F, AuthToken] = jsonOf
