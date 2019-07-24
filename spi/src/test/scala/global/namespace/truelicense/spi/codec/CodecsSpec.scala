/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.spi.codec

import java.io.{InputStream, OutputStream}
import java.nio.charset.Charset
import java.util.Optional

import global.namespace.fun.io.api.Socket
import global.namespace.truelicense.api.codec.Codec
import org.scalatest.Matchers._
import org.scalatest.WordSpec
import org.scalatest.prop.TableDrivenPropertyChecks._

class CodecsSpec extends WordSpec {

  "The contentTransferCharset function" should {
    "figure the correct charset" in {
      val table = Table(
        ("contentType", "contentTransferEncoding", "contentTransferCharset"),
        ("foo", "7bit", "us-ascii"),
        ("foo", "quoted-printable", "us-ascii"),
        ("foo", "base64", "us-ascii"),
        ("foo", "8bit", "utf-8"),
        ("foo; charset=iso-8859-1", "8bit", "iso-8859-1"),
        ("application/json", "8bit", "utf-8"),
        ("application/xml; charset=utf-8", "8bit", "utf-8")
      )
      forAll(table) { (contentType, contentTransferEncoding, contentTransferCharset) =>
        val codec = new DummyCodec(contentType, contentTransferEncoding)
        Codecs charset codec shouldBe Optional.of(Charset forName contentTransferCharset)
      }
    }

    "figure no charset" in {
      val table = Table(
        ("contentType", "contentTransferEncoding"),
        ("foo", "binary"),
        ("foo; charset=utf-8", "binary")
      )
      forAll(table) { (contentType, contentTransferEncoding) =>
        val codec = new DummyCodec(contentType, contentTransferEncoding)
        Codecs charset codec shouldBe Optional.empty
      }
    }

    "throw an `IllegalArgumentException`" in {
      val table = Table(
        ("contentType", "contentTransferEncoding"),
        ("foo; charset=bar", "8bit")
      )
      forAll(table) { (contentType, contentTransferEncoding) =>
        val codec = new DummyCodec(contentType, contentTransferEncoding)
        intercept[IllegalArgumentException] {
          Codecs charset codec
        }
      }
    }
  }
}

private class DummyCodec(val contentType: String, val contentTransferEncoding: String) extends Codec {

  def encoder(output: Socket[OutputStream]) = throw new UnsupportedOperationException

  def decoder(input: Socket[InputStream]) = throw new UnsupportedOperationException
}
