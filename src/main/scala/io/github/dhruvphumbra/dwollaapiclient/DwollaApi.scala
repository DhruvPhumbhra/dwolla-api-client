package io.github.dhruvphumbra.dwollaapiclient

import cats.effect.Concurrent
import cats.effect.std.AtomicCell
import cats.syntax.all.*
import io.circe.Decoder
import org.http4s.circe.*
import org.http4s.headers.{Accept, Authorization, `Content-Type`}
import org.http4s.{Headers, *}
import org.http4s.dsl.io.*
import io.circe.literal.json
import io.circe.Json
import io.github.dhruvphumbra.dwollaapiclient.models.*
import org.typelevel.ci.CIStringSyntax
import io.chrisdavenport.mules.Cache
import io.github.dhruvphumbra.dwollaapiclient.HttpBroker.HttpException

import java.util.UUID
import scala.concurrent.duration.*

trait DwollaApi[F[_]]:
  def getAuthToken: F[String]
  def createCustomer(customer: CreateCustomerRequest): F[Either[Throwable, Status]]
  def getCustomer(id: UUID): F[Json]
  def listAndSearchCustomers(req: ListAndSearchCustomersRequest): F[Json]

object DwollaApi:
  def apply[F[_]](implicit ev: DwollaApi[F]): DwollaApi[F] = ev

  def impl[F[_]: Concurrent](httpBroker: HttpBroker[F])(config: Config): F[DwollaApi[F]] = AtomicCell[F].empty[String].map { authTokenCell =>
    new DwollaApi[F]:
      private val baseUri: Uri = Env.getBaseUri(config.env)

      override def getAuthToken: F[String] =
        authTokenCell
          .get
          .flatMap(s => if (s.isEmpty) refreshAccessToken else s.pure[F])

      private def refreshAccessToken: F[String] =
        authTokenCell.evalUpdateAndGet { _ =>
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
            .recoverWith {
              case HttpException(_, _, _, Status.Unauthorized, _, _) =>
                refreshAccessToken.flatMap(_ => refreshAccessToken)
            }
        }


      override def createCustomer(customer: CreateCustomerRequest): F[Either[Throwable, Status]] =
        getAuthToken.flatMap { token =>
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
  }


