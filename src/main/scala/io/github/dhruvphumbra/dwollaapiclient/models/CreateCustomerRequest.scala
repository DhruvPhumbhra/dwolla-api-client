package io.github.dhruvphumbra.dwollaapiclient.models

import cats.effect.Concurrent
import io.circe.derivation.{Configuration, ConfiguredEncoder, ConfiguredEnumCodec, ConfiguredEnumEncoder}
import io.circe.literal.json
import io.circe.{Decoder, Encoder, Json}
import org.http4s.circe.*
import org.http4s.*

object CreateCustomerRequest:
  // TODO: This is a hack. Revisit when [this issue](https://github.com/circe/circe/issues/1126) is addressed.
  given Encoder[CreateCustomerRequest] =
    ConfiguredEncoder
      .derive[CreateCustomerRequest](discriminator = Some("__requestType"))
      .mapJson(j => j.deepDropNullValues.mapObject(_.remove("__requestType")))
  given[F[_] : Concurrent]: EntityDecoder[F, CreateCustomerRequest] = jsonOf

  given[F[_] : Concurrent]: EntityEncoder[F, CreateCustomerRequest] = jsonEncoderOf
enum CreateCustomerRequest derives Decoder:
  case CreateReceiveOnlyCustomerRequest(
                                         firstName: String,
                                         lastName: String,
                                         email: String,
                                         ipAddress: Option[String] = None,
                                         correlationId: Option[String] = None,
                                         businessName: Option[String] = None,
                                         private val `type`: String = "receive-only",
                                       )

  case CreateUnverifiedCustomerRequest(
                                        firstName: String,
                                        lastName: String,
                                        email: String,
                                        ipAddress: Option[String] = None,
                                        correlationId: Option[String] = None,
                                        businessName: Option[String] = None,
                                      )

  case CreateVerifiedPersonalCustomerRequest(
                                              firstName: String,
                                              lastName: String,
                                              email: String,
                                              address1: String,
                                              city: String,
                                              state: String,
                                              postalCode: String,
                                              dateOfBirth: String,
                                              ssn: String,
                                              ipAddress: Option[String] = None,
                                              correlationId: Option[String] = None,
                                              address2: Option[String] = None,
                                              phone: Option[String] = None,
                                              private val `type`: String = "personal",
                                            )

  case CreateVerifiedSoleProprietorshipCustomerRequest(
                                                        firstName: String,
                                                        lastName: String,
                                                        email: String,
                                                        address1: String,
                                                        city: String,
                                                        state: String,
                                                        postalCode: String,
                                                        dateOfBirth: String,
                                                        ssn: String,
                                                        businessName: String,
                                                        businessClassification: String,
                                                        address2: Option[String] = None,
                                                        doingBusinessAs: Option[String] = None,
                                                        ein: Option[String] = None,
                                                        website: Option[String] = None,
                                                        phone: Option[String] = None,
                                                        ipAddress: Option[String] = None,
                                                        correlationId: Option[String] = None,
                                                        private val businessType: String = "soleProprietorship",
                                                        private val `type`: String = "business",
                                                      )

  case CreateVerifiedBusinessCustomerRequest(
                                              firstName: String,
                                              lastName: String,
                                              email: String,
                                              address1: String,
                                              city: String,
                                              state: String,
                                              postalCode: String,
                                              dateOfBirth: String,
                                              ssn: String,
                                              businessName: String,
                                              businessClassification: String,
                                              businessType: String,
                                              ein: String,
                                              controller: BusinessController,
                                              address2: Option[String] = None,
                                              doingBusinessAs: Option[String] = None,
                                              website: Option[String] = None,
                                              phone: Option[String] = None,
                                              ipAddress: Option[String] = None,
                                              correlationId: Option[String] = None,
                                              private val `type`: String = "business",
                                            )

final case class ControllerAddress(
                                    address1: String,
                                    address2: Option[String] = None,
                                    address3: Option[String] = None,
                                    city: String,
                                    stateProvinceRegion: String,
                                    postalCode: Option[String],
                                    country: String,
                                  ) derives Decoder, Encoder.AsObject

final case class ControllerPassport(
                                     number: Option[String],
                                     country: Option[String],
                                   ) derives Decoder, Encoder.AsObject

final case class BusinessController(
                                     firstName: String,
                                     lastName: String,
                                     title: String,
                                     dateOfBirth: String,
                                     address: ControllerAddress,
                                     passport: Option[ControllerPassport],
                                     ssn: Option[String] = None,
                                   ) derives Decoder, Encoder.AsObject