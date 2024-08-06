package io.github.dhruvphumbra.dwollaapiclient.examples

import cats.Parallel
import cats.effect.std.Console
import cats.effect.{Async, Concurrent, ExitCode, IO, IOApp, Temporal}
import cats.syntax.all.*
import cats.effect.syntax.resource.*
import io.circe.syntax.*
import io.github.dhruvphumbra.dwollaapiclient.*
import io.github.dhruvphumbra.dwollaapiclient.models.*
import io.chrisdavenport.mules.{MemoryCache, TimeSpec}
import io.github.dhruvphumbra.dwollaapiclient.models.common.{BusinessController, ControllerAddress, Link}
import io.github.dhruvphumbra.dwollaapiclient.models.requests.*
import io.github.dhruvphumbra.dwollaapiclient.models.responses.*
import org.http4s.{QueryParamEncoder, Uri}
import org.http4s.ember.client.EmberClientBuilder
import org.http4s.implicits.uri

import java.util.UUID
import scala.concurrent.duration.DurationInt

import cats.*
import cats.effect.std.Console
import cats.effect.*
import cats.syntax.all.*
import cats.effect.syntax.resource.*
import cats.effect.kernel.syntax.all.*

//object TestApp extends ResourceApp:
//  def doSomething[F[_] : Applicative : Console : Parallel](): Resource[F, Unit] =
//    (5.pure[F], 6.pure[F]).parFlatMapN((x, y) => Console[F].println(s"${x + y}")).toResource
//
//  def doSomethingIO(): Resource[IO, Unit] =
//    (IO(1), IO(2)).parMapN((x, y) => println(s"${x + y}")).toResource
//
//  override def run(args: List[String]): Resource[IO, ExitCode] =
//    doSomething[IO]()
//      .flatMap(_ => doSomethingIO())
//      .as(ExitCode.Success)

object Main extends IOApp:
  override def run(args: List[String]): IO[ExitCode] =
    runImpl[IO](ArgumentParser.impl.parse(args))

  def runImpl[F[_] : Async : Parallel : Console](config: Config): F[ExitCode] = {
    for {
        client <- EmberClientBuilder.default[F].build
//        cache <- MemoryCache.ofSingleImmutableMap[F, String, AuthToken](TimeSpec.fromDuration(59.minutes)).toResource
        dwollaAuthorizer <- DwollaAuthorizer[F](client, config.baseUri)
        dwollaAuthMiddleware = DwollaAuthMiddleware[F](dwollaAuthorizer, config.clientId, config.clientSecret, client)
        httpBrokerAlg = HttpBroker.impl[F](dwollaAuthMiddleware)
        dwollaApiAlg = DwollaApi.impl[F](httpBrokerAlg, config.baseUri)

//        accountDetails <- dwollaApiAlg.getAccountDetails(UUID.fromString("a066fff3-93ce-48e1-a496-b3b0e9e89b7d")).toResource
//        _ = println(s"account details are $accountDetails")
//
//        badFs <- dwollaApiAlg.listFundingSourcesForAccount(UUID.fromString("87e2cc08-e317-4ee2-807c-0101b8ae9a81"), None).toResource
//        _ = println(s"bad fs details are $badFs")
//
//        ucr <- dwollaApiAlg.createCustomer(CreateCustomerRequest.CreateReceiveOnlyCustomerRequest("first", "last", "fake7@email.com")).toResource
//        _ = println(s"UCR creation response is $ucr")
//
//        ro <- dwollaApiAlg.createCustomer(CreateCustomerRequest.CreateReceiveOnlyCustomerRequest("first", "last", "fake10@email.com", Some("biz name"))).toResource
//        getRo <- ro.map(dwollaApiAlg.getCustomer).sequence.toResource
//        _ = println(s"created RO is $getRo")
//
//              vpc <- dwollaApiAlg.createCustomer(CreateCustomerRequest.CreateVerifiedPersonalCustomerRequest("first", "last", "vpc@email.com", "123 main st", "Chicago", "IL", "60123", "1990-01-01", "6789")).toResource
//              _ = println(s"VPC creation response status is $vpc")
//
//              sp <- dwollaApiAlg.createCustomer(
//                CreateCustomerRequest.CreateVerifiedSoleProprietorshipCustomerRequest("first", "last", "sp+1@email.com", "123 main st", "Chicago", "IL", "60123", "1990-01-01", "6789", "SP biz", "9ed3f670-7d6f-11e3-b1ce-5404a6144203")
//              ).toResource
//              _ = println(s"SP creation response status is $sp")
//
//              llc <- dwollaApiAlg.createCustomer(
//                CreateCustomerRequest.CreateVerifiedBusinessCustomerRequest(
//                  "first",
//                  "last",
//                  "llc3@email.com",
//                  "123 main st",
//                  "Chicago",
//                  "IL",
//                  "60123",
//                  "1990-01-01",
//                  "6789",
//                  "SP biz",
//                  "9ed3f670-7d6f-11e3-b1ce-5404a6144203",
//                  "llc",
//                  "00-0000000",
//                  BusinessController("cfirst", "clast", "CEO", Some("1990-02-02"), ControllerAddress("456 2nd st", None, None, "Chicago", "IL", Some("60456"), "US"), None, Some("6789"))
//                )
//              ).toResource
//              _ = println(s"LLC creation response status is $llc")
//
//        get <- dwollaApiAlg.getCustomer(UUID.fromString("540a8a5e-1998-47b1-85f1-8fdd41989786")).toResource
//        _ = println(s"get customer response is $get")
//
//        fs <- dwollaApiAlg.createFundingSourceForCustomer(
//          UUID.fromString("1e47f98b-8f58-4df6-86fb-80604f071b0f"),
//            CreateFundingSourceRequest(
//              accountNumber = "68839835664", routingNumber = "071101307", `type` = Checking, name = "Test Bank"
//            )
//          ).toResource
//        _ = println(s"created funding source is $fs")
//
//        getFs <- dwollaApiAlg.getFundingSource(UUID.fromString("c899b4a8-0026-4df5-81c5-ab952a2e285c")).toResource
//        _ = println(s"get funding source is $getFs")
//
//        updateFs <- dwollaApiAlg.updateFundingSource(UUID.fromString("c899b4a8-0026-4df5-81c5-ab952a2e285c"), UpdateFundingSourceRequest(name = Some("New Bank 2"))).toResource
//        _ = println(s"update funding source is $updateFs")
//
//        createTx <- dwollaApiAlg.createTransfer(
//          TransferRequest.AchTransferRequest(
//            _links = Map(
//              "source" -> Link(uri"https://api-devint.dwolla.com/funding-sources/97b77540-2aee-41ee-8196-97536911d9ab"),
//              "destination" -> Link(uri"https://api-devint.dwolla.com/funding-sources/31c555b3-aa03-4577-919e-3377d406094a")
//            ),
//            amount = Amount(currency = "USD", value = "1.00"),
//            metadata = Some(Map("metadata" -> "test metadata")),
//            fees = None,
//            clearing = Some(Clearing(source = Some(ClearingOptions.Standard), destination = Some(ClearingOptions.NextAvailable))),
//            achDetails = None, //Some(AchDetails(source = Some(Map("addenda" -> Addenda(List("source addenda")))), destination = Some(Map("addenda" -> Addenda(List("destination addenda")))))),
//            correlationId = Some("some-correlation-id")
//          ),
//          Some("some-ik")
//        ).toResource
//        _ = println(s"created tx is $createTx")

//        createRtpTx <- dwollaApiAlg.createTransfer(
//          TransferRequest.RtpTransferRequest(
//            _links = Map(
//              "source" -> Link(uri"https://api-devint.dwolla.com/funding-sources/2df47abe-fc04-47e2-9485-89677f071339"),
//              "destination" -> Link(uri"https://api-devint.dwolla.com/funding-sources/d78e0e8a-9cf5-43c7-8832-5e250adbb440")
//            ),
//            amount = Amount(currency = "USD", value = "1.01"),
//            correlationId = None
//          ),
//          Some("some-rtp-ik")
//        ).toResource
//        _ = println(s"created rtp tx for test case 1 is $createRtpTx")
//
//        createRtpTx2a <- dwollaApiAlg.createTransfer(
//          TransferRequest.RtpTransferRequest(
//            _links = Map(
//              "source" -> Link(uri"https://api-devint.dwolla.com/funding-sources/2df47abe-fc04-47e2-9485-89677f071339"),
//              "destination" -> Link(uri"https://api-devint.dwolla.com/funding-sources/98e35f47-8e26-4604-863f-6f848e187208")
//            ),
//            amount = Amount(currency = "USD", value = "1.02"),
//            correlationId = None
//          ),
//          Some("some-rtp-ik")
//        ).toResource
//        _ = println(s"created rtp tx for test case 2a is $createRtpTx2a")
//
//        createRtpTx2b <- dwollaApiAlg.createTransfer(
//          TransferRequest.RtpTransferRequest(
//            _links = Map(
//              "source" -> Link(uri"https://api-devint.dwolla.com/funding-sources/2df47abe-fc04-47e2-9485-89677f071339"),
//              "destination" -> Link(uri"https://api-devint.dwolla.com/funding-sources/1c25b4db-e751-4168-a944-aaff5451b2b7")
//            ),
//            amount = Amount(currency = "USD", value = "1.03"),
//            correlationId = None
//          ),
//          Some("some-rtp-ik")
//        ).toResource
//        _ = println(s"created rtp tx for test case 2b is $createRtpTx2b")
//
//        createRtpTx4 <- dwollaApiAlg.createTransfer(
//          TransferRequest.RtpTransferRequest(
//            _links = Map(
//              "source" -> Link(uri"https://api-devint.dwolla.com/funding-sources/2df47abe-fc04-47e2-9485-89677f071339"),
//              "destination" -> Link(uri"https://api-devint.dwolla.com/funding-sources/e8b3e7ad-b8b8-469d-8995-9efe03eccbc7")
//            ),
//            amount = Amount(currency = "USD", value = "1.04"),
//            correlationId = None
//          ),
//          Some("some-rtp-ik")
//        ).toResource
//        _ = println(s"created rtp tx for test case 4 is $createRtpTx4")
//
//        createRtpTx5a <- dwollaApiAlg.createTransfer(
//          TransferRequest.RtpTransferRequest(
//            _links = Map(
//              "source" -> Link(uri"https://api-devint.dwolla.com/funding-sources/2df47abe-fc04-47e2-9485-89677f071339"),
//              "destination" -> Link(uri"https://api-devint.dwolla.com/funding-sources/a0178591-3ea4-48db-9774-310a5812e5e6")
//            ),
//            amount = Amount(currency = "USD", value = "30001.11"),
//            correlationId = None
//          ),
//          Some("some-rtp-ik")
//        ).toResource
//        _ = println(s"created rtp tx for test case 5a is $createRtpTx5a")
//
//        createRtpTx5b <- dwollaApiAlg.createTransfer(
//          TransferRequest.RtpTransferRequest(
//            _links = Map(
//              "source" -> Link(uri"https://api-devint.dwolla.com/funding-sources/2df47abe-fc04-47e2-9485-89677f071339"),
//              "destination" -> Link(uri"https://api-devint.dwolla.com/funding-sources/bbac1989-028d-425e-950c-6585e5d1bdc4")
//            ),
//            amount = Amount(currency = "USD", value = "30001.12"),
//            correlationId = None
//          ),
//          Some("some-rtp-ik")
//        ).toResource
//        _ = println(s"created rtp tx for test case 5b is $createRtpTx5b")

        _ <- (
          dwollaApiAlg.createTransfer(
            TransferRequest.RtpTransferRequest(
              _links = Map(
                "source" -> Link(uri"https://api-devint.dwolla.com/funding-sources/2df47abe-fc04-47e2-9485-89677f071339"),
                "destination" -> Link(uri"https://api-devint.dwolla.com/funding-sources/d78e0e8a-9cf5-43c7-8832-5e250adbb440")
              ),
              amount = Amount(currency = "USD", value = "1.01"),
              correlationId = None
            ),
            Some("some-rtp-ik-1")
          ),
          dwollaApiAlg.createTransfer(
            TransferRequest.RtpTransferRequest(
              _links = Map(
                "source" -> Link(uri"https://api-devint.dwolla.com/funding-sources/2df47abe-fc04-47e2-9485-89677f071339"),
                "destination" -> Link(uri"https://api-devint.dwolla.com/funding-sources/98e35f47-8e26-4604-863f-6f848e187208")
              ),
              amount = Amount(currency = "USD", value = "1.02"),
              correlationId = None
            ),
            Some("some-rtp-ik-2")
          ),
          dwollaApiAlg.createTransfer(
            TransferRequest.RtpTransferRequest(
              _links = Map(
                "source" -> Link(uri"https://api-devint.dwolla.com/funding-sources/2df47abe-fc04-47e2-9485-89677f071339"),
                "destination" -> Link(uri"https://api-devint.dwolla.com/funding-sources/1c25b4db-e751-4168-a944-aaff5451b2b7")
              ),
              amount = Amount(currency = "USD", value = "1.03"),
              correlationId = None
            ),
            Some("some-rtp-ik-3")
          ),
          dwollaApiAlg.createTransfer(
            TransferRequest.RtpTransferRequest(
              _links = Map(
                "source" -> Link(uri"https://api-devint.dwolla.com/funding-sources/2df47abe-fc04-47e2-9485-89677f071339"),
                "destination" -> Link(uri"https://api-devint.dwolla.com/funding-sources/e8b3e7ad-b8b8-469d-8995-9efe03eccbc7")
              ),
              amount = Amount(currency = "USD", value = "1.04"),
              correlationId = None
            ),
            Some("some-rtp-ik-4")
          ),
          dwollaApiAlg.createTransfer(
            TransferRequest.RtpTransferRequest(
              _links = Map(
                "source" -> Link(uri"https://api-devint.dwolla.com/funding-sources/2df47abe-fc04-47e2-9485-89677f071339"),
                "destination" -> Link(uri"https://api-devint.dwolla.com/funding-sources/a0178591-3ea4-48db-9774-310a5812e5e6")
              ),
              amount = Amount(currency = "USD", value = "30001.11"),
              correlationId = None
            ),
            Some("some-rtp-ik-5")
          ),
          dwollaApiAlg.createTransfer(
            TransferRequest.RtpTransferRequest(
              _links = Map(
                "source" -> Link(uri"https://api-devint.dwolla.com/funding-sources/2df47abe-fc04-47e2-9485-89677f071339"),
                "destination" -> Link(uri"https://api-devint.dwolla.com/funding-sources/bbac1989-028d-425e-950c-6585e5d1bdc4")
              ),
              amount = Amount(currency = "USD", value = "30001.12"),
              correlationId = None
            ),
            Some("some-rtp-ik-6")
          )
        ).parFlatMapN((tx1, tx2a, tx2b, tx4, tx5a, tx5b) => Console[F].println(s"$tx1 \n $tx2a \n $tx2b \n $tx4 \n $tx5a \n $tx5b")).toResource
//
//        listFs <- dwollaApiAlg.listFundingSourceForCustomer(UUID.fromString("1e47f98b-8f58-4df6-86fb-80604f071b0f"), None).toResource
//        _ = println(s"list funding source is $listFs")
//
//        lando <- dwollaApiAlg.listAndSearchCustomers(ListAndSearchCustomersRequest(limit = Some(3), offset = Some(5))).toResource
//        _ = println(s"get customer list response is $lando")
//
//        search <- dwollaApiAlg.listAndSearchCustomers(ListAndSearchCustomersRequest(search = Some("first"))).toResource
//        _ = println(s"get customer list response is $search")
//
//        //      shouldFail <- dwollaApiAlg.listAndSearchCustomers(ListAndSearchCustomersRequest(search = Some("first"), email = Some("llc2@email.com"))).toResource
//        //      _ = println(s"get customer list response is $shouldFail")
//
//        email <- dwollaApiAlg.listAndSearchCustomers(ListAndSearchCustomersRequest(email = Some("llc3@email.com"))).toResource
//        _ = println(s"get customer list response is $email")
//
//              status <- dwollaApiAlg.listAndSearchCustomers(ListAndSearchCustomersRequest(status = Some("retry"))).toResource
//              _ = println(s"get customer list response is $status")

      } yield ()
  }.use_.map(_ => ExitCode.Success)
