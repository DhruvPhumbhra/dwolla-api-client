package io.github.dhruvphumbra.dwollaapiclient.models

import org.http4s.Uri

enum Env:
  case Local
  case Devint
  case Sandbox
  case Production

object Env:
  def apply(s: String): Env = s.toLowerCase match
    case "local" => Local
    case "devint" => Devint
    case "sandbox" => Sandbox
    case "production" => Production
    case _ => throw new IllegalArgumentException("Invalid environment")

  def getBaseUri(e: Env): Uri = e match
    case Env.Local => Uri.unsafeFromString("http://localhost:17188")
    case Env.Devint => Uri.unsafeFromString("https://api-devint.dwolla.com")
    case Env.Sandbox => Uri.unsafeFromString("https://api-sandbox.dwolla.com")
    case Env.Production => Uri.unsafeFromString("https://api.dwolla.com")