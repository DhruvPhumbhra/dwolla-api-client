package io.github.dhruvphumbra.dwollaapiclient

import cats.syntax.all.*
import cats.effect.{Concurrent, Resource, Temporal}
import cats.effect.syntax.resource.*
import io.chrisdavenport.mules.{Cache, MemoryCache, TimeSpec}
import io.github.dhruvphumbra.dwollaapiclient.models.responses.AuthToken
import org.http4s.{BasicCredentials, Headers, MediaType, Method, Request, Uri, UrlForm}
import org.http4s.client.Client
import org.http4s.headers.{Authorization, `Content-Type`}

import scala.concurrent.duration.DurationInt

trait DwollaAuthorizer[F[_]]:
  def fetchAuthToken(clientId: String, clientSecret: String): F[AuthToken]

object DwollaAuthorizer:
  def apply[F[_]: Concurrent: Temporal](client: Client[F], baseUri: Uri): Resource[F, DwollaAuthorizer[F]] = 
    MemoryCache.ofSingleImmutableMap[F, String, AuthToken](TimeSpec.fromDuration(59.minutes)).map { cache =>
      impl(client, baseUri)(cache)
    }.toResource
  
  def impl[F[_] : Concurrent](client: Client[F], baseUri: Uri)(cache: Cache[F, String, AuthToken]): DwollaAuthorizer[F] =
    new DwollaAuthorizer[F]:
      override def fetchAuthToken(clientId: String, clientSecret: String): F[AuthToken] =
        cache
          .lookup("dwolla-token")
          .flatMap { t =>
            t.fold(refreshAuthToken(clientId, clientSecret).flatMap(token => cache.insert("dwolla-token", token).as(token)))(_.pure[F])
          }

      private def refreshAuthToken(clientId: String, clientSecret: String): F[AuthToken] =
        client.expect[AuthToken](
          Request[F](
            Method.POST,
            baseUri / "token",
            headers = Headers(
              `Content-Type`(MediaType.application.`x-www-form-urlencoded`),
              Authorization(BasicCredentials(clientId, clientSecret)),
            )
          ).withEntity(UrlForm("grant_type" -> "client_credentials"))
        )
