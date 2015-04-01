/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package net.java.truelicense.obfuscate

import java.util.regex.Pattern
import org.junit.runner._
import org.scalatest._
import org.scalatest.junit._
import org.scalatest.Matchers._
import org.scalatest.prop.PropertyChecks._

/** @author Christian Schlichtherle */
@RunWith(classOf[JUnitRunner])
class ObfuscatedStringTest extends WordSpec {

  import ObfuscatedStringTest._

  "An ObfuscatedString" should {
    "always reproduce the original string literal" in {
      // This string has been obfuscated with Oracle's JDK 1.7.0_05 on the
      // Windows platform.
      // It must produce the same string across all Java implementations
      // and platforms.
      new ObfuscatedString(
        Array[Long](0x41e62204839b3a26l, 0x72fabb78cac1945al, 0x2e0928a3b6bcb12cl)
      ).toString should equal ("Hello world!")
    }

    "support round trip conversion" in {
      forAll(
        Table(
          "string literal",
          "Hello world!",
          "\"",
          "\\",
          ""
        )
      ) { s =>
        val p = Pattern compile (
          "^\\Qnew net.java.truelicense.obfuscate.ObfuscatedString(new long[] { \\E(.*)\\Q }).toString() /* => \"" + escape(s) + "\" */\\E$",
          Pattern.DOTALL)
        val o = ObfuscatedString.obfuscate(s)
        val m = p.matcher(o)
        m.matches should equal (true)
        val es = m group 1 split "\\s*,\\s*"
        val el = new Array[Long](es.length)
        for (i <- 0 until el.length) {
          val ds = es(i)//.toLowerCase
          ds should startWith ("0x")
          ds should endWith ("l")
          val l = ds substring (2, ds.length - 1)
          el(i) = parseLong(l)
        }
        val os = new ObfuscatedString(el)
        val d = os.toString
        d should not be theSameInstanceAs (s)
        d should equal (s)
        os.toCharArray should equal (d.toCharArray)
      }
    }
  }
}

/** @author Christian Schlichtherle */
object ObfuscatedStringTest {

  private def escape(s: String) =
    s.replaceAll("\\\\", "\\\\\\\\").replaceAll("\"", "\\\\\"")

  /**
   * `java.lang.Long.parseLong` doesn't work if the highest bit is set in the
   * string to parse. Hence a simplified version is provided here.
   */
  private def parseLong(s: String) = {
    val l = s.length
    if (16 < l) throw new NumberFormatException(s)
    var result = 0L
    for (i <- 0 until l) {
      val digit = Character digit (s charAt i, 16)
      if (digit < 0 || digit >= 16)
        throw new NumberFormatException("For input string: \"" + s + "\"")
      result *= 16
      result += digit
    }
    result
  }
}
