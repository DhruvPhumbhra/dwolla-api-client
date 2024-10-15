package io.github.dhruvphumbra.dwollaapiclient.models.requests

import cats.effect.Concurrent
import io.circe.derivation.{ConfiguredEncoder, renaming}
import io.circe.{Codec, Decoder, Encoder}
import io.github.dhruvphumbra.dwollaapiclient.stringEnumCodec
import org.http4s.EntityEncoder
import org.http4s.circe.jsonEncoderOf

case class UpdateFundingSourceRequest(
                                       accountNumber: Option[String] = None,
                                       routingNumber: Option[String] = None,
                                       `type`: Option[FundingSourceType] = None,
                                       name: Option[String] = None
                                     ) derives Encoder.AsObject

object UpdateFundingSourceRequest:
    given [F[_] : Concurrent]: EntityEncoder[F, UpdateFundingSourceRequest] = jsonEncoderOf

object FundingSourceRequest:
  given Encoder[FundingSourceRequest] =
  ConfiguredEncoder
    .derive[FundingSourceRequest](discriminator = Some("__requestType"))
    .mapJson(_.deepDropNullValues.mapObject(_.remove("__requestType")))

  given[F[_] : Concurrent]: EntityEncoder[F, FundingSourceRequest] = jsonEncoderOf

enum FundingSourceRequest:
  case CreateFundingSourceRequest(
                                   accountNumber: String,
                                   routingNumber: String,
                                   `type`: FundingSourceType,
                                   name: String
                                 )

object FundingSourceType:
  given Codec[FundingSourceType] = stringEnumCodec[FundingSourceType](renaming.kebabCase)

enum FundingSourceType:
  case Checking
  case Savings
