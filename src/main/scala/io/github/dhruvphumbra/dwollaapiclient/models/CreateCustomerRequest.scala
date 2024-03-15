package io.github.dhruvphumbra.dwollaapiclient.models

import cats.effect.Concurrent
import io.circe.derivation.{Configuration, ConfiguredEnumCodec, ConfiguredEnumEncoder}
import io.circe.literal.json
import io.circe.{Decoder, Encoder, Json}
import org.http4s.circe.*
import org.http4s.*

//sealed trait CreateCustomerRequest derives Decoder, Encoder.AsObject:
//  val firstName: String
//  val lastName: String
//  val email: String
//  val ipAddress: Option[String]
//  val correlationId: Option[String]
//
//object CreateCustomerRequest:
//  given Encoder.AsObject[CreateCustomerRequest] = Encoder.AsObject.derived(using Configuration.default.withDiscriminator("_type"))
//  given Configuration = Configuration.default.withDiscriminator("_type").withKebabCaseMemberNames
//
//  //    given Encoder[CreateCustomerRequest] = new Encoder[CreateCustomerRequest]:
//  //      override def apply(a: CreateCustomerRequest): Json = a match
//  //        json"""{ "firstName": ${a.firstName}, "lastName": ${a.lastName}, "email": ${a.email} }"""
//  given[F[_] : Concurrent]: EntityDecoder[F, CreateCustomerRequest] = jsonOf
//
//  given[F[_] : Concurrent]: EntityEncoder[F, CreateCustomerRequest] = jsonEncoderOf
//
//final case class CreateReceiveOnlyCustomerRequest(
//                                                   override val firstName: String,
//                                                   override val lastName: String,
//                                                   override val email: String,
//                                                   override val ipAddress: Option[String] = None,
//                                                   override val correlationId: Option[String] = None,
//                                                   businessName: Option[String] = None,
//                                                   private val `type`: String = "receive-only",
//                                                 ) extends CreateCustomerRequest derives Decoder, Encoder.AsObject
//
//object CreateReceiveOnlyCustomerRequest:
//  given Configuration = Configuration.default.withDiscriminator("_type").withKebabCaseMemberNames
//  given[F[_] : Concurrent]: EntityDecoder[F, CreateReceiveOnlyCustomerRequest] = jsonOf
//
//  given[F[_] : Concurrent]: EntityEncoder[F, CreateReceiveOnlyCustomerRequest] = jsonEncoderOf
//
//final case class CreateUnverifiedCustomerRequest(
//                                                  override val firstName: String,
//                                                  override val lastName: String,
//                                                  override val email: String,
//                                                  override val ipAddress: Option[String] = None,
//                                                  override val correlationId: Option[String] = None,
//                                                  businessName: Option[String] = None,
//                                                ) extends CreateCustomerRequest derives Decoder, Encoder.AsObject
//
//object CreateUnverifiedCustomerRequest:
//  given Configuration = Configuration.default.withDiscriminator("_type").withKebabCaseMemberNames
//  given[F[_] : Concurrent]: EntityDecoder[F, CreateUnverifiedCustomerRequest] = jsonOf
//
//  given[F[_] : Concurrent]: EntityEncoder[F, CreateUnverifiedCustomerRequest] = jsonEncoderOf
//
//final case class CreateVerifiedPersonalCustomerRequest(
//                                                        override val firstName: String,
//                                                        override val lastName: String,
//                                                        override val email: String,
//                                                        `type`: String,
//                                                        address1: String,
//                                                        city: String,
//                                                        state: String,
//                                                        postalCode: String,
//                                                        dateOfBirth: String,
//                                                        ssn: String,
//                                                        override val ipAddress: Option[String] = None,
//                                                        override val correlationId: Option[String] = None,
//                                                        address2: Option[String] = None,
//                                                        phone: Option[String] = None,
//                                                        businessName: Option[String] = None,
//                                                      ) extends CreateCustomerRequest derives Decoder, Encoder.AsObject
//
//object CreateVerifiedPersonalCustomerRequest:
//  given[F[_] : Concurrent]: EntityDecoder[F, CreateVerifiedPersonalCustomerRequest] = jsonOf
//
//  given[F[_] : Concurrent]: EntityEncoder[F, CreateVerifiedPersonalCustomerRequest] = jsonEncoderOf
//
//final case class CreateVerifiedSoleProprietorshipCustomerRequest(
//                                                                  override val firstName: String,
//                                                                  override val lastName: String,
//                                                                  override val email: String,
//                                                                  address1: String,
//                                                                  city: String,
//                                                                  state: String,
//                                                                  postalCode: String,
//                                                                  dateOfBirth: String,
//                                                                  ssn: String,
//                                                                  businessName: String,
//                                                                  businessClassification: String,
//                                                                  address2: Option[String] = None,
//                                                                  doingBusinessAs: Option[String] = None,
//                                                                  ein: Option[String] = None,
//                                                                  website: Option[String] = None,
//                                                                  phone: Option[String] = None,
//                                                                  override val ipAddress: Option[String] = None,
//                                                                  override val correlationId: Option[String] = None,
//                                                                  private val businessType: String = "soleProprietorship",
//                                                                  private val `type`: String = "business",
//                                                                ) extends CreateCustomerRequest derives Decoder, Encoder.AsObject
//
//object CreateVerifiedSoleProprietorshipCustomerRequest:
//  given[F[_] : Concurrent]: EntityDecoder[F, CreateVerifiedSoleProprietorshipCustomerRequest] = jsonOf
//
//  given[F[_] : Concurrent]: EntityEncoder[F, CreateVerifiedSoleProprietorshipCustomerRequest] = jsonEncoderOf
//
//final case class ControllerAddress(
//                                    address1: String,
//                                    address2: Option[String],
//                                    address3: Option[String],
//                                    city: String,
//                                    stateProvinceRegion: String,
//                                    postalCode: Option[String],
//                                    country: String
//                                  )derives Decoder, Encoder.AsObject
//
//object ControllerAddress:
//  given[F[_] : Concurrent]: EntityDecoder[F, ControllerAddress] = jsonOf
//
//  given[F[_] : Concurrent]: EntityEncoder[F, ControllerAddress] = jsonEncoderOf
//
//final case class ControllerPassport(
//                                     number: Option[String],
//                                     country: Option[String],
//                                   )derives Decoder, Encoder.AsObject
//
//final case class BusinessController(
//                                     firstName: String,
//                                     lastName: String,
//                                     title: String,
//                                     dateOfBirth: String,
//                                     address: ControllerAddress,
//                                     passport: Option[ControllerPassport],
//                                     ssn: Option[String] = None,
//                                   )derives Decoder, Encoder.AsObject
//
//object BusinessController:
//  given[F[_] : Concurrent]: EntityDecoder[F, BusinessController] = jsonOf
//
//  given[F[_] : Concurrent]: EntityEncoder[F, BusinessController] = jsonEncoderOf
//
//final case class CreateVerifiedBusinessCustomerRequest(
//                                                        override val firstName: String,
//                                                        override val lastName: String,
//                                                        override val email: String,
//                                                        address1: String,
//                                                        city: String,
//                                                        state: String,
//                                                        postalCode: String,
//                                                        dateOfBirth: String,
//                                                        ssn: String,
//                                                        businessName: String,
//                                                        businessType: String,
//                                                        businessClassification: String,
//                                                        ein: String,
//                                                        address2: Option[String] = None,
//                                                        doingBusinessAs: Option[String] = None,
//                                                        website: Option[String] = None,
//                                                        phone: Option[String] = None,
//                                                        override val ipAddress: Option[String] = None,
//                                                        override val correlationId: Option[String] = None,
//                                                        controller: Option[BusinessController] = None,
//                                                        private val `type`: String = "business",
//                                                      ) extends CreateCustomerRequest derives Decoder, Encoder.AsObject
//
//object CreateVerifiedBusinessCustomerRequest:
//  given[F[_] : Concurrent]: EntityDecoder[F, CreateVerifiedBusinessCustomerRequest] = jsonOf
//
//  given[F[_] : Concurrent]: EntityEncoder[F, CreateVerifiedBusinessCustomerRequest] = jsonEncoderOf

object CreateCustomerRequest:
  given Configuration = Configuration.default.withDiscriminator("_type").withKebabCaseMemberNames
  given[F[_] : Concurrent]: EntityDecoder[F, CreateCustomerRequest] = jsonOf

  given[F[_] : Concurrent]: EntityEncoder[F, CreateCustomerRequest] = jsonEncoderOf
enum CreateCustomerRequest derives Decoder, ConfiguredEnumEncoder:
  //  val firstName: String
  //  val lastName: String
  //  val email: String
  //  val ipAddress: Option[String]
  //  val correlationId: Option[String]
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

