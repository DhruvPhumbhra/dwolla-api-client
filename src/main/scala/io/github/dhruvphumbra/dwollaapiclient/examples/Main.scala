package io.github.dhruvphumbra.dwollaapiclient.examples

import cats.effect.{Async, Concurrent, ExitCode, IO, IOApp, Temporal}
import cats.syntax.all.*
import cats.effect.syntax.resource.*
import io.circe.syntax.*
import io.github.dhruvphumbra.dwollaapiclient.*
import io.github.dhruvphumbra.dwollaapiclient.models.*
import io.chrisdavenport.mules.{MemoryCache, TimeSpec}
import io.github.dhruvphumbra.dwollaapiclient.models.FundingSourceRequest.CreateFundingSourceRequest
import io.github.dhruvphumbra.dwollaapiclient.models.FundingSourceType.Checking
import io.github.dhruvphumbra.dwollaapiclient.models.TransferRequest.AchTransferRequest
import org.http4s.{QueryParamEncoder, Uri}
import org.http4s.ember.client.EmberClientBuilder
import org.http4s.implicits.uri

import java.util.UUID
import scala.concurrent.duration.DurationInt

object Main extends IOApp:
  override def run(args: List[String]): IO[ExitCode] =
    runImpl[IO](ArgumentParser.impl.parse(args))

  def runImpl[F[_] : Async](config: Config): F[ExitCode] = {
    for {
        client <- EmberClientBuilder.default[F].build
        cache <- MemoryCache.ofSingleImmutableMap[F, String, AuthToken](TimeSpec.fromDuration(59.minutes)).toResource
        dwollaAuthorizer = DwollaAuthorizer.impl[F](client, config.baseUri)(cache)
        dwollaAuthMiddleware = DwollaAuthMiddleware[F](dwollaAuthorizer, config.clientId, config.clientSecret, client)
        httpBrokerAlg = HttpBroker.impl[F](dwollaAuthMiddleware)
        dwollaApiAlg = DwollaApi.impl[F](httpBrokerAlg, config.baseUri)

        accountDetails <- dwollaApiAlg.getAccountDetails(UUID.fromString("a066fff3-93ce-48e1-a496-b3b0e9e89b7d")).toResource
        _ = println(s"account details are $accountDetails")

//        badAccount <- dwollaApiAlg.getAccountDetails(UUID.fromString("87e2cc08-e317-4ee2-807c-0101b8ae9a81")).toResource
//        _ = println(s"bad account details are $badAccount")

//        badFs <- dwollaApiAlg.listFundingSourcesForAccount(UUID.fromString("87e2cc08-e317-4ee2-807c-0101b8ae9a81"), None).toResource
//        _ = println(s"bad fs details are $badFs")

//        token <- (1 to 10).toList.map(_ => dwollaApiAlg.getAuthToken).sequence.toResource
//        _ = println(s"token is $token")


//        ucr <- dwollaApiAlg.createCustomer(CreateCustomerRequest.CreateReceiveOnlyCustomerRequest("first", "last", "fake7@email.com")).toResource
//        _ = println(s"UCR creation response is $ucr")
        //
        //      ro <- dwollaApiAlg.createCustomer(CreateCustomerRequest.CreateReceiveOnlyCustomerRequest("first", "last", "fake6@email.com", Some("biz name"))).toResource
        //      _ = println(s"RO creation response status is $ro")
        //
        //      vpc <- dwollaApiAlg.createCustomer(CreateCustomerRequest.CreateVerifiedPersonalCustomerRequest("first", "last", "vpc@email.com", "123 main st", "Chicago", "IL", "60123", "1990-01-01", "6789")).toResource
        //      _ = println(s"VPC creation response status is $vpc")
        //
        //      sp <- dwollaApiAlg.createCustomer(
        //        CreateCustomerRequest.CreateVerifiedSoleProprietorshipCustomerRequest("first", "last", "sp@email.com", "123 main st", "Chicago", "IL", "60123", "1990-01-01", "6789", "SP biz", "9ed3f670-7d6f-11e3-b1ce-5404a6144203")
        //      ).toResource
        //      _ = println(s"SP creation response status is $sp")
        //
        //      llc <- dwollaApiAlg.createCustomer(
        //        CreateCustomerRequest.CreateVerifiedBusinessCustomerRequest(
        //          "first",
        //          "last",
        //          "llc2@email.com",
        //          "123 main st",
        //          "Chicago",
        //          "IL",
        //          "60123",
        //          "1990-01-01",
        //          "6789",
        //          "SP biz",
        //          "9ed3f670-7d6f-11e3-b1ce-5404a6144203",
        //          "llc",
        //          "00-0000000",
        //          BusinessController("cfirst", "clast", "CEO", "1990-02-02", ControllerAddress("456 2nd st", None, None, "Chicago", "IL", Some("60456"), "US"), None, Some("6789"))
        //        )
        //      ).toResource
        //      _ = println(s"LLC creation response status is $llc")

//        get <- dwollaApiAlg.getCustomer(UUID.fromString("1e47f98b-8f58-4df6-86fb-80604f071b0f")).toResource
//        _ = println(s"get customer response is $get")

//        fs <- dwollaApiAlg.createFundingSourceForCustomer(
//          UUID.fromString("1e47f98b-8f58-4df6-86fb-80604f071b0f"),
//            CreateFundingSourceRequest(
//              accountNumber = "68839835664", routingNumber = "071101307", `type` = Checking, name = "Test Bank"
//            )
//          ).toResource
//        _ = println(s"created funding source is $fs")

//        getFs <- dwollaApiAlg.getFundingSource(UUID.fromString("c899b4a8-0026-4df5-81c5-ab952a2e285c")).toResource
//        _ = println(s"get funding source is $getFs")
//          x = UpdateFundingSourceRequest(name = Some("Newer Bank")).asJson
//          _ = println(x)
//
//
        updateFs <- dwollaApiAlg.updateFundingSource(UUID.fromString("c899b4a8-0026-4df5-81c5-ab952a2e285c"), UpdateFundingSourceRequest(`type` = Some(FundingSourceType.Checking))).toResource
        _ = println(s"update funding source is $updateFs")

//        createTx <- dwollaApiAlg.createTransfer(
//          AchTransferRequest(
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

//        listFs <- dwollaApiAlg.listFundingSourceForCustomer(UUID.fromString("1e47f98b-8f58-4df6-86fb-80604f071b0f"), None).toResource
//        _ = println(s"list funding source is $listFs")
//
//        lando <- dwollaApiAlg.listAndSearchCustomers(ListAndSearchCustomersRequest(limit = Some(1), offset = Some(5))).toResource
//        _ = println(s"get customer list response is $lando")
//
//        search <- dwollaApiAlg.listAndSearchCustomers(ListAndSearchCustomersRequest(search = Some("first"))).toResource
//        _ = println(s"get customer list response is $search")
//
//        //      shouldFail <- dwollaApiAlg.listAndSearchCustomers(ListAndSearchCustomersRequest(search = Some("first"), email = Some("llc2@email.com"))).toResource
//        //      _ = println(s"get customer list response is $shouldFail")
//
//        email <- dwollaApiAlg.listAndSearchCustomers(ListAndSearchCustomersRequest(email = Some("llc2@email.com"))).toResource
//        _ = println(s"get customer list response is $email")

        //      status <- dwollaApiAlg.listAndSearchCustomers(ListAndSearchCustomersRequest(status = Some("retry"))).toResource
        //      _ = println(s"get customer list response is $status")

      } yield ()
  }.use_.map(_ => ExitCode.Success)
