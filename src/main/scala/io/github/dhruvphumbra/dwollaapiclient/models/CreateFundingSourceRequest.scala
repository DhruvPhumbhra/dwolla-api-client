package io.github.dhruvphumbra.dwollaapiclient.models

import cats.effect.Concurrent
import io.circe.derivation.ConfiguredEncoder
import io.circe.{Codec, Decoder, Encoder}
import io.github.dhruvphumbra.dwollaapiclient.stringEnumCodec
import org.http4s.circe.{jsonEncoderOf, jsonOf}
import org.http4s.{EntityDecoder, EntityEncoder}

object CreateFundingSourceRequest:
  given Encoder[CreateFundingSourceRequest] =
  ConfiguredEncoder
    .derive[CreateFundingSourceRequest](discriminator = Some("__requestType"))
    .mapJson(j => j.deepDropNullValues.mapObject(_.remove("__requestType")))

  given[F[_] : Concurrent]: EntityDecoder[F, CreateFundingSourceRequest] = jsonOf

  given[F[_] : Concurrent]: EntityEncoder[F, CreateFundingSourceRequest] = jsonEncoderOf

enum CreateFundingSourceRequest derives Decoder:
  case FundingSourceRequest(
                             accountNumber: String,
                             routingNumber: String,
                             `type`: FundingSourceType,
                             name: String
                           )

object FundingSourceType:
  given Codec[FundingSourceType] = stringEnumCodec[FundingSourceType]

enum FundingSourceType:
  case Checking
  case Savings
