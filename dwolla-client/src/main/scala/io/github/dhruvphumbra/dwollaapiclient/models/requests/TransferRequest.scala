package io.github.dhruvphumbra.dwollaapiclient.models.requests

import cats.effect.kernel.Concurrent
import io.circe.derivation.{ConfiguredEncoder, renaming}
import io.circe.{Codec, Decoder, Encoder}
import io.github.dhruvphumbra.dwollaapiclient.models.*
import io.github.dhruvphumbra.dwollaapiclient.models.common.Link
import io.github.dhruvphumbra.dwollaapiclient.stringEnumCodec
import org.http4s.circe.{decodeUri, encodeUri, jsonEncoderOf}
import org.http4s.{EntityEncoder, Uri}

object TransferRequest:
  given Encoder[TransferRequest] =
    ConfiguredEncoder
      .derive[TransferRequest](discriminator = Some("__requestType"))
      .mapJson(_.deepDropNullValues.mapObject(_.remove("__requestType")))

  given [F[_] : Concurrent]: EntityEncoder[F, TransferRequest] = jsonEncoderOf


enum TransferRequest:
  case AchTransferRequest(
                           _links: Map[String, Link],
                           amount: Amount,
                           metadata: Option[Map[String, String]],
                           fees: Option[List[Fee]],
                           clearing: Option[Clearing],
                           achDetails: Option[AchDetails],
                           correlationId: Option[String]
                         )

  case RtpTransferRequest(
                           _links: Map[String, Link],
                           amount: Amount,
                           correlationId: Option[String],
                           private val processingChannel: ProcessingChannel = ProcessingChannel("real-time-payments"),
                         ) //TODO: implement

case class Fee(_links: Map[String, String], amount: Amount) derives Decoder, Encoder.AsObject

case class Amount(currency: String, value: String) derives Decoder, Encoder.AsObject

case class Clearing(source: Option[ClearingOptions], destination: Option[ClearingOptions]) derives Decoder, Encoder.AsObject

object ClearingOptions:
  given Codec[ClearingOptions] = stringEnumCodec[ClearingOptions](renaming.kebabCase)

enum ClearingOptions:
  case NextAvailable
  case Standard

case class AchDetails(source: Option[Map[String, Addenda]], destination: Option[Map[String, Addenda]]) derives Decoder, Encoder.AsObject // TODO: fix encoding

case class Addenda(values: List[String]) derives Decoder, Encoder.AsObject

case class ProcessingChannel(destination: String) derives Decoder, Encoder.AsObject
