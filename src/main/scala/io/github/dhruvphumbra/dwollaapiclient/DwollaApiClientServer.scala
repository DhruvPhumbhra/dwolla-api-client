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


//      helloWorldAlg = HelloWorld.impl[F]
//      jokeAlg = Jokes.impl[F](client)
//
//      httpApp = (
//        DwollaapiclientRoutes.helloWorldRoutes[F](helloWorldAlg) <+>
//        DwollaapiclientRoutes.jokeRoutes[F](jokeAlg)
//      ).orNotFound
//
//      finalHttpApp = Logger.httpApp(true, true)(httpApp)
//
//      _ <-
//        EmberServerBuilder.default[F]
//          .withHost(ipv4"0.0.0.0")
//          .withPort(port"8080")
//          .withHttpApp(finalHttpApp)
//          .build
    } yield ()
  }.use_.map(_ => ExitCode.Success)
