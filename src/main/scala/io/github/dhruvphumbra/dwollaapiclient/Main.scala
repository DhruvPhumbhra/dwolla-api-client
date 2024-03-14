package io.github.dhruvphumbra.dwollaapiclient

import cats.effect.{IO, IOApp}

object Main extends IOApp.Simple:
  val run = DwollaapiclientServer.run[IO]
