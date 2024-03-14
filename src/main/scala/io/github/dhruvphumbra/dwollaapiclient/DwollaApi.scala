package io.github.dhruvphumbra.dwollaapiclient

import cats.effect.Concurrent
import cats.syntax.all.*
import io.circe.Decoder
import org.http4s.circe.*
import org.http4s.headers.{Authorization, `Content-Type`}
import org.http4s.*
import io.circe.literal.json

trait DwollaApi[F[_]]:
  def getAccessToken: F[String]

case class AuthToken(access_token: String) derives Decoder
object AuthToken:
  given [F[_]: Concurrent]: EntityDecoder[F, AuthToken] = jsonOf

//opaque type BaseUri = Uri
object DwollaApi:
  def apply[F[_]](implicit ev: DwollaApi[F]): DwollaApi[F] = ev

  def impl[F[_]: Concurrent](httpBroker: HttpBroker[F])(config: Config) = new DwollaApi[F]:

    private val baseUri: Uri = Env.getBaseUri(config.env)

    override def getAccessToken: F[String] =
      httpBroker
        .makeRequest[AuthToken](
          Request[F](
            Method.POST,
            baseUri / "token",
            headers = Headers(
              `Content-Type`(MediaType.application.`x-www-form-urlencoded`),
              Authorization(BasicCredentials(config.clientId, config.clientSecret))
            )
          ).withEntity(UrlForm("grant_type" -> "client_credentials"))
        )
        .map(_.access_token)
