package io.github.dhruvphumbra.dwollaapiclient

import cats.effect.{Async, ExitCode, Resource}
import cats.syntax.all.*
import com.comcast.ip4s.*
import org.http4s.{Request, Uri}
import org.http4s.ember.client.EmberClientBuilder
import org.http4s.implicits.*
import cats.effect.syntax.resource.*
import io.github.dhruvphumbra.dwollaapiclient.models.*

object DwollaApiClientServer:

  def run[F[_]: Async](args: List[String]): F[ExitCode] = {
    for {
      client <- EmberClientBuilder.default[F].build
      argumentParser = ArgumentParser.impl
      config = argumentParser.parse(args)

      httpBrokerAlg = HttpBroker.impl[F](client)

      dwollaApiAlg = DwollaApi.impl[F](httpBrokerAlg)(config)

      token <- dwollaApiAlg.getAccessToken.toResource
      _ = println(s"token is $token")

      ucr <- dwollaApiAlg.createCustomer(CreateCustomerRequest.CreateReceiveOnlyCustomerRequest("first", "last", "fake5@email.com")).toResource
      _ = println(s"UCR creation response is $ucr")

      ro <- dwollaApiAlg.createCustomer(CreateCustomerRequest.CreateReceiveOnlyCustomerRequest("first", "last", "fake6@email.com", Some("biz name"))).toResource
      _ = println(s"RO creation response status is $ro")

      vpc <- dwollaApiAlg.createCustomer(CreateCustomerRequest.CreateVerifiedPersonalCustomerRequest("first", "last", "vpc@email.com", "123 main st", "Chicago", "IL", "60123", "1990-01-01", "6789")).toResource
      _ = println(s"VPC creation response status is $vpc")

      sp <- dwollaApiAlg.createCustomer(
        CreateCustomerRequest.CreateVerifiedSoleProprietorshipCustomerRequest("first", "last", "sp@email.com", "123 main st", "Chicago", "IL", "60123", "1990-01-01", "6789", "SP biz", "9ed3f670-7d6f-11e3-b1ce-5404a6144203")
      ).toResource
      _ = println(s"SP creation response status is $sp")

      llc <- dwollaApiAlg.createCustomer(
        CreateCustomerRequest.CreateVerifiedBusinessCustomerRequest(
          "first",
          "last",
          "llc2@email.com",
          "123 main st",
          "Chicago",
          "IL",
          "60123",
          "1990-01-01",
          "6789",
          "SP biz",
          "9ed3f670-7d6f-11e3-b1ce-5404a6144203",
          "llc",
          "00-0000000",
          BusinessController("cfirst", "clast", "CEO", "1990-02-02", ControllerAddress("456 2nd st", None, None, "Chicago", "IL", Some("60456"), "US"), None, Some("6789"))
        )
      ).toResource
      _ = println(s"LLC creation response status is $llc")

    } yield ()
  }.use_.map(_ => ExitCode.Success)
