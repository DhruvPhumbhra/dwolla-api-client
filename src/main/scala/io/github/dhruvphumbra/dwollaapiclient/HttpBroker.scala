package io.github.dhruvphumbra.dwollaapiclient

import cats.effect.Concurrent
import cats.syntax.all.*
import org.http4s.Method.GET
import org.http4s.client.Client
import org.http4s.{EntityDecoder, EntityEncoder, Header, Method, Request, Uri}
import org.typelevel.ci.CIStringSyntax

import java.util.UUID

trait HttpBroker[F[_]]:
  def get[T](uri: Uri)(implicit decoder: EntityDecoder[F, T]): F[T]
  def createAndGetResourceId[T](uri: Uri, entity: T, hds: Header.Raw*)(implicit encoder: EntityEncoder[F, T]): F[Option[UUID]]
  def update[T, U](uri: Uri, entity: T)(implicit entityEncoder: EntityEncoder[F, T], entityDecoder: EntityDecoder[F, U]): F[U]

object HttpBroker:
  def apply[F[_]](implicit ev: HttpBroker[F]): HttpBroker[F] = ev

  def impl[F[_]: Concurrent](C: Client[F]): HttpBroker[F] = new HttpBroker[F]:

    override def get[T](uri: Uri)(implicit decoder: EntityDecoder[F, T]): F[T] =
      C.expect[T](Request[F](Method.GET, uri))

    override def createAndGetResourceId[T](uri: Uri, entity: T, hds: Header.Raw*)(implicit encoder: EntityEncoder[F, T]): F[Option[UUID]] =
      C.run(Request[F](Method.POST, uri).withEntity(entity).withHeaders(hds)).use { res =>
        res.headers.headers
          .find(_.name == ci"Location")
          .map(h => UUID.fromString(h.value.split("/").last))
          .pure[F]
      }

    override def update[T, U](uri: Uri, entity: T)(implicit entityEncoder: EntityEncoder[F, T], entityDecoder: EntityDecoder[F, U]): F[U] =
      C.expect[U](Request[F](Method.POST, uri).withEntity(entity))
