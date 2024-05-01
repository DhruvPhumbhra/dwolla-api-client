package io.github.dhruvphumbra

import io.circe.{Codec, Decoder, Encoder}

import scala.compiletime.{constValue, summonAll}
import scala.deriving.Mirror

package object dwollaapiclient:
  inline def stringEnumCodec[T](transformNames: String => String)(using m: Mirror.SumOf[T]): Codec[T] =
    val elems = summonAll[Tuple.Map[m.MirroredElemTypes, ValueOf]]
      .productIterator.asInstanceOf[Iterator[ValueOf[T]]].map(_.value)
    val names = summonAll[Tuple.Map[m.MirroredElemLabels, ValueOf]]
      .productIterator.asInstanceOf[Iterator[ValueOf[String]]].map(s => transformNames(s.value))
    val encoderMap = (elems zip names).toMap
    val decoderMap = (names zip elems).toMap

    Codec.from[T](
      Decoder[String].emap { name =>
        decoderMap.get(name).fold(Left(s"Name $name is invalid value"))(Right(_))
      },
      Encoder[String].contramap[T](encoderMap.apply)
    )

//  def coproductConfiguredEncoder[A <: reflect.Enum : Mirror.SumOf : Mirror.ProductOf]: Encoder[A] =
//    ConfiguredEncoder
//      .derive[A](discriminator = Some("__requestType"))(implicitly[Mirror.Of[A]])
//      .mapJson(_.deepDropNullValues.mapObject(_.remove("__requestType")))