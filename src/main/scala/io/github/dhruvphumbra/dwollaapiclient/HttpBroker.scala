package io.github.dhruvphumbra.dwollaapiclient

import cats.{Applicative, Monad}
import cats.effect.Concurrent
import cats.effect.kernel.Async
import cats.syntax.all.*
import org.http4s.client.Client
import org.http4s.{EntityDecoder, Headers, Method, Request, Response, Status, Uri}

trait HttpBroker[F[_]]:
  def makeRequest[T](req: Request[F])(implicit decoder: EntityDecoder[F, T]): F[T]

object HttpBroker:
  def apply[F[_]](implicit ev: HttpBroker[F]): HttpBroker[F] = ev

  final case class HttpException(
    uri: Uri,
    method: Method,
    headers: Headers,
    status: Status,
    body: String
  ) extends scala.util.control.NoStackTrace {

    override def getMessage: String = s"uri: $uri \n method: $method \n status: $status \n headers: $headers \n body: $body"
  }

  def impl[F[_]: Concurrent](C: Client[F]) = new HttpBroker[F]:
    override def makeRequest[T](req: Request[F])(implicit decoder: EntityDecoder[F, T]): F[T] =
      C.expectOr[T](req)(toHttpException(req, _))

    private def toHttpException(request: Request[F], response: Response[F]): F[Throwable] = {
      val body = response.body.through(fs2.text.utf8.decode).compile.string
      body.map(HttpException(request.uri, request.method, response.headers, response.status, _))
    }
