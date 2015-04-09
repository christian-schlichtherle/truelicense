/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.maven.plugin

import org.junit.runner.RunWith
import org.scalatest.Matchers._
import org.scalatest.WordSpec
import org.scalatest.junit.JUnitRunner
import org.scalatest.prop.PropertyChecks._

/**
 * @author Christian Schlichtherle
 */
@RunWith(classOf[JUnitRunner])
class ScalaToolTest extends WordSpec /*with Matchers*/ {

  "A ScalaTool " should {
    "generate an obfuscated string " which {
      "matches the pattern" in {
        forAll(Table(
          "string",
          "",
          "Hello world!"
        )) { string =>
          new ScalaTool obfuscatedString string should (fullyMatch regex "new org\\.truelicense\\.obfuscate\\.ObfuscatedString\\(Array\\[Long\\]\\([^)]*\\)\\) /\\*.*\\*/")
        }
      }
    }
  }
}
