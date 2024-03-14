package io.github.dhruvphumbra.dwollaapiclient

import cats.effect.{Async, ExitCode, Resource}
import cats.syntax.all.*
import com.comcast.ip4s.*
import org.http4s.{Request, Uri}
import org.http4s.ember.client.EmberClientBuilder
import org.http4s.implicits.*
import cats.effect.syntax.resource._

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
  }.useForever.map(_ => ExitCode.Success)
