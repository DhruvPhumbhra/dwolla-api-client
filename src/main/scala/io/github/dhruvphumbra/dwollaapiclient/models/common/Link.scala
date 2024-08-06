package io.github.dhruvphumbra.dwollaapiclient.models.common

import io.circe.{Decoder, Encoder}
import org.http4s.Uri
import org.http4s.circe.{decodeUri, encodeUri}

case class Link(href: Uri, `type`: Option[String] = None, `resource-type`: Option[String] = None) derives Decoder, Encoder.AsObject
