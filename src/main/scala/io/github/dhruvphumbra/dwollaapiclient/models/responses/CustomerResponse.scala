package io.github.dhruvphumbra.dwollaapiclient.models.responses

import cats.effect.Concurrent
import io.circe.Decoder.Result
import io.circe.{Decoder, HCursor}
import io.github.dhruvphumbra.dwollaapiclient.models.common.*
import org.http4s.EntityDecoder
import org.http4s.circe.jsonOf

case class CustomerResponse(
                             customer: Customer,
                             errors: List[CustomerError]
                           )

object CustomerResponse:
  given Decoder[CustomerResponse] = new Decoder[CustomerResponse]:
    override def apply(c: HCursor): Result[CustomerResponse] =
      for {
        customer <- Decoder[Customer].apply(c)
        errors <- c.downField("_embedded").get[List[CustomerError]]("errors")
      } yield CustomerResponse(customer, errors)

  given [F[_] : Concurrent]: EntityDecoder[F, CustomerResponse] = jsonOf
