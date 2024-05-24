package io.github.dhruvphumbra.dwollaapiclient

import cats.Applicative
import cats.syntax.all.*
import cats.effect.syntax.resource.*
import cats.effect.{MonadCancelThrow, Resource}
import org.http4s.{AuthScheme, Credentials, Header, MediaType}
import org.http4s.client.Client
import org.http4s.headers.{Authorization, `Content-Type`}
import org.http4s.{Headers, *}
import org.typelevel.ci.CIStringSyntax

object DwollaAuthMiddleware:
  def apply[F[_]: MonadCancelThrow](authorizer: DwollaAuthorizer[F], clientId: String, clientSecret: String, client: Client[F]): Client[F] =
    Client[F] { req =>
      for {
        token <- authorizer.fetchAuthToken(clientId, clientSecret).toResource
        headers = req.headers ++ Headers(
          Header.Raw(ci"Accept", "application/vnd.dwolla.v1.hal+json"),
          `Content-Type`(MediaType.application.`json`),
          Authorization(Credentials.Token(AuthScheme.Bearer, token.access_token))
        )
        res <- client.run(req.withHeaders(headers))
      } yield res
    }
