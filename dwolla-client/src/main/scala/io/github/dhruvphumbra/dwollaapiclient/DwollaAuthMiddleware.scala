package io.github.dhruvphumbra.dwollaapiclient

import cats.effect.MonadCancelThrow
import cats.effect.kernel.Async
import cats.effect.syntax.resource.*
import org.http4s.client.Client
import org.http4s.client.middleware.Logger
import org.http4s.headers.{Authorization, `Content-Type`}
import org.http4s.{AuthScheme, Credentials, Header, Headers, MediaType, *}
import org.typelevel.ci.CIStringSyntax

object DwollaAuthMiddleware:
  def apply[F[_]: Async: MonadCancelThrow](authorizer: DwollaAuthorizer[F], clientId: String, clientSecret: String, client: Client[F]): Client[F] =
    Client[F] { req =>
      for {
        token <- authorizer.fetchAuthToken(clientId, clientSecret).toResource
        headers = req.headers ++ Headers(
          Header.Raw(ci"Accept", "application/vnd.dwolla.v1.hal+json"),
          `Content-Type`(MediaType.application.`json`),
          Authorization(Credentials.Token(AuthScheme.Bearer, token.access_token))
        )
        res <- Logger(logHeaders = true, logBody = true)(client).run(req.withHeaders(headers))
      } yield res
    }
