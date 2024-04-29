package io.github.dhruvphumbra.dwollaapiclient.models

import cats.effect.Concurrent
import io.circe.{Decoder, Encoder, Json, Codec}
import io.circe.derivation.ConfiguredEncoder
import org.http4s.{EntityDecoder, EntityEncoder}
import org.http4s.circe.{jsonEncoderOf, jsonOf}
import scala.compiletime.summonAll
import scala.deriving.Mirror

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
  given Decoder[FundingSourceType] = stringEnumDecoder[FundingSourceType]
  given Encoder[FundingSourceType] = stringEnumEncoder[FundingSourceType]

enum FundingSourceType:
  case Checking
  case Savings

inline def stringEnumDecoder[T](using m: Mirror.SumOf[T]): Decoder[T] =
  val elemInstances = summonAll[Tuple.Map[m.MirroredElemTypes, ValueOf]]
    .productIterator.asInstanceOf[Iterator[ValueOf[T]]].map(_.value)
  val elemNames = summonAll[Tuple.Map[m.MirroredElemLabels, ValueOf]]
    .productIterator.asInstanceOf[Iterator[ValueOf[String]]].map(_.value.toLowerCase)
  val mapping = (elemNames zip elemInstances).toMap
  Decoder[String].emap { name =>
    mapping.get(name).fold(Left(s"Name $name is invalid value"))(Right(_))
  }

inline def stringEnumEncoder[T](using m: Mirror.SumOf[T]): Encoder[T] =
  val elemInstances = summonAll[Tuple.Map[m.MirroredElemTypes, ValueOf]]
    .productIterator.asInstanceOf[Iterator[ValueOf[T]]].map(_.value)
  val elemNames = summonAll[Tuple.Map[m.MirroredElemLabels, ValueOf]]
    .productIterator.asInstanceOf[Iterator[ValueOf[String]]].map(_.value.toLowerCase)
  val mapping = (elemInstances zip elemNames).toMap
  Encoder[String].contramap[T](mapping.apply)
