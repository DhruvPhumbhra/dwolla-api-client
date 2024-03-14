package io.github.dhruvphumbra.dwollaapiclient

import scala.annotation.tailrec

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

case class Config(env: Env, clientId: String, clientSecret: String)

trait ArgumentParser:
  def parse(args: List[String]): Config

object ArgumentParser:
  def apply(implicit ev: ArgumentParser) = ev

  def impl: ArgumentParser = new ArgumentParser:
    override def parse(args: List[String]): Config = toConfig(parseOptions(args))

    @tailrec
    private def parseOptions(args: List[String], options: Map[String, String] = Map.empty): Map[String, String] = {
      val isValidOption = option => List("--env", "--client-id", "--client-secret").contains(option)
      args match
        case Nil => options
        // TODO: This case seems to be broken. Never triggers.
        case option :: Nil if !isValidOption(option) => throw new IllegalArgumentException(s"No value provided for option $option")
        case "--env" :: value :: tail => parseOptions(tail, options ++ Map("env" -> value))
        case "--client-id" :: value :: tail => parseOptions(tail, options ++ Map("clientId" -> value))
        case "--client-secret" :: value :: tail => parseOptions(tail, options ++ Map("clientSecret" -> value))
        case option :: _ => throw new IllegalArgumentException(s"Unsupported option $option")
    }

    private def toConfig(options: Map[String, String]): Config = {
      Config(Env(options.getOrElse("env", "local")), options("clientId"), options("clientSecret"))
    }
