package io.github.dhruvphumbra.dwollaapiclient.models.common

import io.circe.{Decoder, Encoder}

final case class BusinessController(
                                     firstName: String,
                                     lastName: String,
                                     title: String,
                                     dateOfBirth: Option[String],
                                     address: ControllerAddress,
                                     passport: Option[ControllerPassport],
                                     ssn: Option[String] = None,
                                   ) derives Decoder, Encoder.AsObject

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
