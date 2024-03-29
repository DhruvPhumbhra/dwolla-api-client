package io.github.dhruvphumbra.dwollaapiclient

import cats.effect.{ExitCode, IO, IOApp}

object Main extends IOApp:
  override def run(args: List[String]): IO[ExitCode] = DwollaApiClientServer.run[IO](args)
