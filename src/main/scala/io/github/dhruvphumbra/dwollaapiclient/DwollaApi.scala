package io.github.dhruvphumbra.dwollaapiclient

import cats.effect.Concurrent
import io.circe.Json
import io.github.dhruvphumbra.dwollaapiclient.models.*
import org.http4s.circe.*
import org.http4s.dsl.io.*
import org.http4s.*
import org.typelevel.ci.CIStringSyntax

import java.util.UUID

trait DwollaApi[F[_]]:
  def getAccountDetails(id: UUID): F[Json]
  def listFundingSourcesForAccount(id: UUID, removed: Option[Boolean]): F[Json]
  def createCustomer(customer: CreateCustomerRequest): F[Either[Throwable, UUID]]
  def getCustomer(id: UUID): F[Json]
  def listAndSearchCustomers(req: ListAndSearchCustomersRequest): F[Json]
  def createFundingSourceForCustomer(id: UUID, req: FundingSourceRequest): F[Either[Throwable, UUID]]
  def getFundingSource(id: UUID): F[Json]
  def listFundingSourceForCustomer(id: UUID, removed: Option[Boolean]): F[Json]
  def updateFundingSource(id: UUID, req: UpdateFundingSourceRequest): F[Json]
  def createTransfer(req: TransferRequest, ik: Option[String]): F[Either[Throwable, UUID]]
  def getTransfer(id: UUID): F[Json]

object DwollaApi:
  def apply[F[_]](implicit ev: DwollaApi[F]): DwollaApi[F] = ev

  def impl[F[_] : Concurrent](httpBroker: HttpBroker[F], baseUri: Uri): DwollaApi[F] =
    new DwollaApi[F]:

      override def getAccountDetails(id: UUID): F[Json] =
        httpBroker.get[Json](baseUri / "accounts" / id)

      override def listFundingSourcesForAccount(id: UUID, removed: Option[Boolean]): F[Json] =
        httpBroker.get[Json](baseUri / "accounts" / id / "funding-sources" +?? ("removed", removed))

      override def createCustomer(customer: CreateCustomerRequest): F[Either[Throwable, UUID]] =
        httpBroker.createAndGetResourceId(baseUri / "customers", customer)

      override def getCustomer(id: UUID): F[Json] =
        httpBroker.get[Json](baseUri / "customers" / id)

      override def listAndSearchCustomers(req: ListAndSearchCustomersRequest): F[Json] =
        httpBroker
          .get[Json](
            baseUri / "customers"
              +?? ("limit", req.limit)
              +?? ("offset", req.offset)
              +?? ("search", req.search)
              +?? ("email", req.email)
              +?? ("status", req.status)
          )

      override def createFundingSourceForCustomer(id: UUID, fs: FundingSourceRequest): F[Either[Throwable, UUID]] =
        httpBroker.createAndGetResourceId(baseUri / "customers" / id / "funding-sources", fs)

      override def getFundingSource(id: UUID): F[Json] =
        httpBroker.get[Json](baseUri / "funding-sources" / id)

      override def listFundingSourceForCustomer(id: UUID, removed: Option[Boolean]): F[Json] =
        httpBroker.get[Json](baseUri / "customers" / id / "funding-sources" +?? ("removed", removed))

      override def updateFundingSource(id: UUID, req: UpdateFundingSourceRequest): F[Json] =
        httpBroker.update[UpdateFundingSourceRequest, Json](baseUri / "funding-sources" / id, req)

      override def createTransfer(req: TransferRequest, ik: Option[String]): F[Either[Throwable, UUID]] =
        httpBroker.createAndGetResourceId(baseUri / "transfers", req, ik.map(Header.Raw(ci"Idempotency-Key", _)).toList: _*)

      override def getTransfer(id: UUID): F[Json] =
        httpBroker.get[Json](baseUri / "transfers" / id)