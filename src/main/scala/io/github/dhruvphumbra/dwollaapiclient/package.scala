package io.github.dhruvphumbra

import io.circe.{Codec, Decoder, Encoder}

import scala.compiletime.summonAll
import scala.deriving.Mirror

package object dwollaapiclient:
  inline def stringEnumCodec[T](using m: Mirror.SumOf[T]): Codec[T] =
    val elems = summonAll[Tuple.Map[m.MirroredElemTypes, ValueOf]]
      .productIterator.asInstanceOf[Iterator[ValueOf[T]]].map(_.value)
    val names = summonAll[Tuple.Map[m.MirroredElemLabels, ValueOf]]
      .productIterator.asInstanceOf[Iterator[ValueOf[String]]].map(_.value.toLowerCase)
    val encoderMap = (elems zip names).toMap
    val decoderMap = (names zip elems).toMap

    Codec.from[T](
      Decoder[String].emap { name =>
        decoderMap.get(name).fold(Left(s"Name $name is invalid value"))(Right(_))
      },
      Encoder[String].contramap[T](encoderMap.apply)
    )
