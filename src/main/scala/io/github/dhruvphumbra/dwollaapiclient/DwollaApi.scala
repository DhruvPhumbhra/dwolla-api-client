package io.github.dhruvphumbra.dwollaapiclient

import cats.effect.Concurrent
import cats.syntax.all.*
import io.chrisdavenport.mules.Cache
import io.circe.Json
import io.github.dhruvphumbra.dwollaapiclient.HttpBroker.HttpException
import io.github.dhruvphumbra.dwollaapiclient.models.*
import org.http4s.circe.*
import org.http4s.dsl.io.*
import org.http4s.headers.{Authorization, `Content-Type`}
import org.http4s.{Headers, *}
import org.typelevel.ci.CIStringSyntax

import java.util.UUID

trait DwollaApi[F[_]]:
  def getAuthToken: F[String]
  def createCustomer(customer: CreateCustomerRequest): F[Either[Throwable, UUID]]
  def getCustomer(id: UUID): F[Json]
  def listAndSearchCustomers(req: ListAndSearchCustomersRequest): F[Json]
  def createFundingSourceForCustomer(id: UUID, req: FundingSourceRequest): F[Either[Throwable, UUID]]
  def getFundingSource(id: UUID): F[Json]
  def listFundingSourceForCustomer(id: UUID, removed: Option[Boolean] = None): F[Json]
  def updateFundingSource(id: UUID, req: UpdateFundingSourceRequest): F[Json]
  def createTransfer(req: TransferRequest, ik: Option[String]): F[Either[Throwable, UUID]]

object DwollaApi:
  def apply[F[_]](implicit ev: DwollaApi[F]): DwollaApi[F] = ev

  def impl[F[_] : Concurrent](httpBroker: HttpBroker[F])(config: Config, cache: Cache[F, String, String]): DwollaApi[F] =
    new DwollaApi[F]:
      private val baseUri: Uri = Env.getBaseUri(config.env)

      override def getAuthToken: F[String] =
        cache
          .lookup("token")
          .flatMap { t =>
            t.fold(refreshAccessToken.flatMap(token => cache.insert("token", token).as(token)))(_.pure[F])
          }

      private def refreshAccessToken: F[String] =
        httpBroker
          .makeRequest[AuthToken](
            Request[F](
              Method.POST,
              baseUri / "token",
              headers = Headers(
                `Content-Type`(MediaType.application.`x-www-form-urlencoded`),
                Authorization(BasicCredentials(config.clientId, config.clientSecret)),
              )
            ).withEntity(UrlForm("grant_type" -> "client_credentials"))
          )
          .map(_.access_token)

      override def createCustomer(customer: CreateCustomerRequest): F[Either[Throwable, UUID]] =
        getAuthToken.flatMap { token =>
          httpBroker
            .requestAndGetResourceId(
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

      override def getCustomer(id: UUID): F[Json] =
        getAuthToken.flatMap { token =>
          httpBroker
            .makeRequest[Json](
              Request[F](
                Method.GET,
                baseUri / "customers" / id,
                headers = Headers(
                  Header.Raw(ci"Accept", "application/vnd.dwolla.v1.hal+json"),
                  `Content-Type`(MediaType.application.`json`),
                  Authorization(Credentials.Token(AuthScheme.Bearer, token))
                )
              )
            ).recoverWith {
            case HttpException(_, _, _, Status.Unauthorized, _, _) =>
              refreshAccessToken.flatMap(_ => getCustomer(id))
          }
        }

      override def listAndSearchCustomers(req: ListAndSearchCustomersRequest): F[Json] =
        getAuthToken.flatMap { token =>
          httpBroker
            .makeRequest[Json](
              Request[F](
                Method.GET,
                baseUri / "customers"
                  +?? ("limit", req.limit)
                  +?? ("offset", req.offset)
                  +?? ("search", req.search)
                  +?? ("email", req.email)
                  +?? ("status", req.status),
                headers = Headers(
                  Header.Raw(ci"Accept", "application/vnd.dwolla.v1.hal+json"),
                  `Content-Type`(MediaType.application.`json`),
                  Authorization(Credentials.Token(AuthScheme.Bearer, token))
                )
              )
            )
        }

      override def createFundingSourceForCustomer(id: UUID, fs: FundingSourceRequest): F[Either[Throwable, UUID]] =
        getAuthToken.flatMap { token =>
          httpBroker
            .requestAndGetResourceId(
              Request[F](
                Method.POST,
                baseUri / "customers" / id / "funding-sources",
                headers = Headers(
                  Header.Raw(ci"Accept", "application/vnd.dwolla.v1.hal+json"),
                  `Content-Type`(MediaType.application.`json`),
                  Authorization(Credentials.Token(AuthScheme.Bearer, token))
                )
              ).withEntity(fs)
            )
        }

      override def getFundingSource(id: UUID): F[Json] =
        getAuthToken.flatMap { token =>
          httpBroker
            .makeRequest[Json](
              Request[F](
                Method.GET,
                baseUri / "funding-sources" / id,
                headers = Headers(
                  Header.Raw(ci"Accept", "application/vnd.dwolla.v1.hal+json"),
                  `Content-Type`(MediaType.application.`json`),
                  Authorization(Credentials.Token(AuthScheme.Bearer, token))
                )
              )
            )
        }

      override def listFundingSourceForCustomer(id: UUID, removed: Option[Boolean]): F[Json] =
        getAuthToken.flatMap { token =>
          httpBroker
            .makeRequest[Json](
              Request[F](
                Method.GET,
                baseUri / "customers" / id / "funding-sources"
                  +?? ("removed", removed),
                headers = Headers(
                  Header.Raw(ci"Accept", "application/vnd.dwolla.v1.hal+json"),
                  `Content-Type`(MediaType.application.`json`),
                  Authorization(Credentials.Token(AuthScheme.Bearer, token))
                )
              )
            )
        }

      override def updateFundingSource(id: UUID, req: UpdateFundingSourceRequest): F[Json] =
        getAuthToken.flatMap { token =>
          httpBroker
            .makeRequest(
              Request[F](
                Method.POST,
                baseUri / "funding-sources" / id,
                headers = Headers(
                  Header.Raw(ci"Accept", "application/vnd.dwolla.v1.hal+json"),
                  `Content-Type`(MediaType.application.`json`),
                  Authorization(Credentials.Token(AuthScheme.Bearer, token))
                )
              ).withEntity(req)
            )
        }

      override def createTransfer(req: TransferRequest, ik: Option[String]): F[Either[Throwable, UUID]] =
        getAuthToken.flatMap { token =>
          httpBroker
            .requestAndGetResourceId(
              Request[F](
                Method.POST,
                baseUri / "transfers",
                headers = Headers(
                  Header.Raw(ci"Accept", "application/vnd.dwolla.v1.hal+json"),
                  `Content-Type`(MediaType.application.`json`),
                  Authorization(Credentials.Token(AuthScheme.Bearer, token)),
                  ik.map(Header.Raw(ci"Idempotency-Key", _))
                )
              ).withEntity(req)
            )
        }