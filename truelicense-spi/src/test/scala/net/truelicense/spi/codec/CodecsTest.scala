/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.truelicense.spi.codec

import java.nio.charset.Charset

import net.truelicense.api.codec.Codec
import net.truelicense.api.io.{Sink, Source}
import org.junit.runner.RunWith
import org.scalatest.Matchers._
import org.scalatest.WordSpec
import org.scalatest.junit.JUnitRunner
import org.scalatest.prop.PropertyChecks._

/** @author Christian Schlichtherle */
@RunWith(classOf[JUnitRunner])
class CodecsTest extends WordSpec {

  def charset(name: String) = Charset forName name

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
      forAll (table) { (contentType, contentTransferEncoding, contentTransferCharset) =>
        val codec = MockCodec(contentType, contentTransferEncoding)
        Codecs contentTransferCharset codec shouldBe charset(contentTransferCharset)
      }
    }

    "figure no charset" in {
      val table = Table(
        ("contentType", "contentTransferEncoding"),
        ("foo", "binary"),
        ("foo; charset=utf-8", "binary"),
        ("foo; charset=bar", "8bit")
      )
      forAll (table) { (contentType, contentTransferEncoding) =>
        val codec = MockCodec(contentType, contentTransferEncoding)
        intercept[IllegalArgumentException] { Codecs contentTransferCharset codec }
      }
    }
  }
}

/** @author Christian Schlichtherle */
private case class MockCodec(contentType: String, contentTransferEncoding: String)
  extends Codec {

  def encoder(sink: Sink) = throw new UnsupportedOperationException
  def decoder(source: Source) = throw new UnsupportedOperationException
}
