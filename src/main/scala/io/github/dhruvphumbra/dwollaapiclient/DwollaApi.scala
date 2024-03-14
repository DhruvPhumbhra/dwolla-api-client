package io.github.dhruvphumbra.dwollaapiclient

import cats.effect.Concurrent
import cats.syntax.all.*
import io.circe.Decoder
import org.http4s.circe.*
import org.http4s.headers.{Accept, Authorization, `Content-Type`}
import org.http4s.{Headers, *}
import io.circe.literal.json
import io.github.dhruvphumbra.dwollaapiclient.DwollaModels.{AuthToken, CreateCustomerRequest}
import org.typelevel.ci.CIStringSyntax

trait DwollaApi[F[_]]:
  def getAccessToken: F[String]
  def createCustomer(customer: CreateCustomerRequest): F[Either[Throwable, Status]]

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

    override def createCustomer(customer: CreateCustomerRequest): F[Either[Throwable, Status]] =
      getAccessToken.flatMap { token =>
        httpBroker
          .requestAndGetStatus(
            Request[F](
              Method.POST,
              baseUri / "customers",
              headers = Headers(
                Header.Raw(ci"Accept", "application/vnd.dwolla.v1.hal+json"),
                `Content-Type`(MediaType.application.`json`),
                Authorization(Credentials.Token(AuthScheme.Bearer, token))
              )
            ).withEntity(customer)
          )
      }
