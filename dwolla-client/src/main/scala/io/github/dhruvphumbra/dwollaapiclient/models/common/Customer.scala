package io.github.dhruvphumbra.dwollaapiclient.models.common

import io.circe.Decoder

import java.time.ZonedDateTime
import java.util.UUID

final case class Customer(
                           _links: Map[String, Link],
                           id: UUID,
                           firstName: String,
                           lastName: String,
                           email: String,
                           `type`: String,
                           status: String,
                           created: ZonedDateTime,
                           address1: Option[String],
                           address2: Option[String],
                           city: Option[String],
                           state: Option[String],
                           postalCode: Option[String],
                           businessName: Option[String],
                           controller: Option[BusinessController],
                           businessType: Option[String],
                           businessClassification: Option[UUID]
                         )derives Decoder

final case class CustomerError(code: String, message: String, path: String, _links: Map[String, Link]) derives Decoder
