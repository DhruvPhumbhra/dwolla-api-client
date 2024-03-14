package io.github.dhruvphumbra.dwollaapiclient

import cats.effect.Concurrent
import io.circe.literal.json
import io.circe.{Decoder, Encoder, Json}
import org.http4s.circe.*
import org.http4s.*
import io.circe.generic.semiauto.deriveEncoder

object DwollaModels:
  case class AuthToken(access_token: String) derives Decoder
  object AuthToken:
    given[F[_] : Concurrent]: EntityDecoder[F, AuthToken] = jsonOf

  sealed trait CreateCustomerRequest derives Decoder, Encoder.AsObject:
    val firstName: String
    val lastName: String
    val email: String
    val ipAddress: Option[String]
    val correlationId: Option[String]
  object CreateCustomerRequest:
//    given Encoder[CreateCustomerRequest] = new Encoder[CreateCustomerRequest]:
//      override def apply(a: CreateCustomerRequest): Json = a match
//        json"""{ "firstName": ${a.firstName}, "lastName": ${a.lastName}, "email": ${a.email} }"""
    given[F[_] : Concurrent]: EntityDecoder[F, CreateCustomerRequest] = jsonOf
    given[F[_] : Concurrent]: EntityEncoder[F, CreateCustomerRequest] = jsonEncoderOf

  final case class CreateReceiveOnlyCustomerRequest(
    override val firstName: String,
    override val lastName: String,
    override val email: String,
    override val ipAddress: Option[String] = None,
    override val correlationId: Option[String] = None,
    businessName: Option[String] = None,
    private val `type`: String = "receive-only",
  ) extends CreateCustomerRequest derives Decoder, Encoder.AsObject
  object CreateReceiveOnlyCustomerRequest:
    given[F[_] : Concurrent]: EntityDecoder[F, CreateReceiveOnlyCustomerRequest] = jsonOf
    given[F[_] : Concurrent]: EntityEncoder[F, CreateReceiveOnlyCustomerRequest] = jsonEncoderOf

  final case class CreateUnverifiedCustomerRequest(
    override val firstName: String,
    override val lastName: String,
    override val email: String,
    override val ipAddress: Option[String] = None,
    override val correlationId: Option[String] = None,
    businessName: Option[String] = None,
  ) extends CreateCustomerRequest derives Decoder, Encoder.AsObject
  object CreateUnverifiedCustomerRequest:
    given[F[_] : Concurrent]: EntityDecoder[F, CreateUnverifiedCustomerRequest] = jsonOf
    given[F[_] : Concurrent]: EntityEncoder[F, CreateUnverifiedCustomerRequest] = jsonEncoderOf
