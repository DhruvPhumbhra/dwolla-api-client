package io.github.dhruvphumbra.dwollaapiclient

import cats.effect.{Async, Resource}
import cats.syntax.all.*
import com.comcast.ip4s.*
import org.http4s.{Request, Uri}
import org.http4s.ember.client.EmberClientBuilder
import org.http4s.implicits.*

object DwollaapiclientServer:

  def run[F[_]: Async]: F[Nothing] = {
    for {
      client <- EmberClientBuilder.default[F].build
      httpBrokerAlg = HttpBroker.impl[F](client)
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
  }.useForever
