/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package global.namespace.truelicense.maven.plugin.generation

import org.scalatest.Matchers._
import org.scalatest.WordSpec
import org.scalatest.prop.PropertyChecks._

/**
 * @author Christian Schlichtherle
 */
class JavaToolSpec extends WordSpec {

  "A JavaTool " should {
    "generate an obfuscated string " which {
      "matches the pattern" in {
        forAll(Table(
          "string",
          "",
          "Hello world!"
        )) { string =>
          new JavaTool obfuscatedString string should (fullyMatch regex "new global\\.namespace\\.truelicense\\.obfuscate\\.ObfuscatedString\\(new long\\[\\] \\{ [^}]* \\}\\) /\\*.*\\*/")
        }
      }
    }
  }
}
