package io.github.dhruvphumbra.dwollaapiclient

import cats.data.*
import cats.effect.Concurrent
import cats.syntax.all.*
import org.http4s.Method.GET
import org.http4s.client.Client
import org.http4s.{EntityDecoder, EntityEncoder, Header, Headers, Method, Request, Response, Status, Uri}
import org.typelevel.ci.CIStringSyntax

import java.util.UUID

private [dwollaapiclient] trait HttpBroker[F[_]]:
  def get[T](uri: Uri)(implicit decoder: EntityDecoder[F, T]): F[T]
  def createAndGetResourceId[T](uri: Uri, entity: T, hds: Header.Raw*)(implicit encoder: EntityEncoder[F, T]): F[Either[Throwable, UUID]]
  def update[T, U](uri: Uri, entity: T)(implicit entityEncoder: EntityEncoder[F, T], entityDecoder: EntityDecoder[F, U]): F[U]

private [dwollaapiclient] object HttpBroker:
  def apply[F[_]](implicit ev: HttpBroker[F]): HttpBroker[F] = ev

  final case class HttpException(
    uri: Uri,
    method: Method,
    headers: Headers,
    status: Status,
    body: String,
    reqBody: String // TODO: Remove after debugging
  ) extends scala.util.control.NoStackTrace {

    override def getMessage: String = s"uri: $uri \n method: $method \n status: $status \n headers: $headers \n body: $body \n reqBody: $reqBody"
  }

  def impl[F[_]: Concurrent](C: Client[F]): HttpBroker[F] = new HttpBroker[F]:

    override def get[T](uri: Uri)(implicit decoder: EntityDecoder[F, T]): F[T] =
      val req = Request[F](Method.GET, uri)
      C.expectOr[T](req)(toHttpException(req, _))

    override def createAndGetResourceId[T](uri: Uri, entity: T, hds: Header.Raw*)(implicit encoder: EntityEncoder[F, T]): F[Either[Throwable, UUID]] =
      val req = Request[F](Method.POST, uri).withEntity(entity)
      C.run(req.withHeaders(hds)).use { res =>
        EitherT.fromOptionM(
          res.headers.headers
            .find(_.name == ci"Location")
            .map(h => UUID.fromString(h.value.split("/").last))
            .pure[F],
          toHttpException(req, res)
        ).value
      }

    override def update[T, U](uri: Uri, entity: T)(implicit entityEncoder: EntityEncoder[F, T], entityDecoder: EntityDecoder[F, U]): F[U] =
      val req = Request[F](Method.GET, uri).withEntity(entity)
      C.expectOr[U](req)(toHttpException(req, _))

    private def toHttpException(request: Request[F], response: Response[F]): F[Throwable] = {
      val body = response.body.through(fs2.text.utf8.decode).compile.string
      val reqBody = request.body.through(fs2.text.utf8.decode).compile.string
      (reqBody, body).mapN((re, rs) => HttpException(request.uri, request.method, response.headers, response.status, rs, re))
    }
