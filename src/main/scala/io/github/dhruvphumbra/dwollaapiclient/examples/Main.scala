package io.github.dhruvphumbra.dwollaapiclient.examples

import cats.effect.{Async, Concurrent, ExitCode, IO, IOApp, Temporal}
import cats.syntax.all.*
import cats.effect.syntax.resource.*
import io.github.dhruvphumbra.dwollaapiclient.{ArgumentParser, Config, DwollaApi, HttpBroker}
import io.github.dhruvphumbra.dwollaapiclient.models.{BusinessController, ControllerAddress, CreateCustomerRequest, ListAndSearchCustomersRequest}
import io.chrisdavenport.mules.{MemoryCache, TimeSpec}
import io.github.dhruvphumbra.dwollaapiclient.models.CreateFundingSourceRequest.FundingSourceRequest
import io.github.dhruvphumbra.dwollaapiclient.models.FundingSourceType.Checking
import org.http4s.QueryParamEncoder
import org.http4s.ember.client.EmberClientBuilder

import java.util.UUID
import scala.concurrent.duration.DurationInt

object Main extends IOApp:
  override def run(args: List[String]): IO[ExitCode] = {
    val argumentParser = ArgumentParser.impl
    val config = argumentParser.parse(args)
    runImpl[IO](config)
  }

  def runImpl[F[_] : Async](config: Config): F[ExitCode] = {
    for {
        client <- EmberClientBuilder.default[F].build
        c <- MemoryCache.ofSingleImmutableMap[F, String, String](TimeSpec.fromDuration(59.minutes)).toResource
        httpBrokerAlg = HttpBroker.impl[F](client)
        dwollaApiAlg = DwollaApi.impl[F](httpBrokerAlg)(config, c)

//        token <- (1 to 10).toList.map(_ => dwollaApiAlg.getAuthToken).sequence.toResource
//        _ = println(s"token is $token")

        //
        //      ucr <- dwollaApiAlg.createCustomer(CreateCustomerRequest.CreateReceiveOnlyCustomerRequest("first", "last", "fake5@email.com")).toResource
        //      _ = println(s"UCR creation response is $ucr")
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
//            FundingSourceRequest(
//              accountNumber = "68839835684", routingNumber = "071101307", `type` = Checking, name = "Test Bank"
//            )
//          ).toResource
//        _ = println(s"created funding source is $fs")

        listFs <- dwollaApiAlg.listFundingSourceForCustomer(UUID.fromString("1e47f98b-8f58-4df6-86fb-80604f071b0f")).toResource
        _ = println(s"list funding source is $listFs")
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
