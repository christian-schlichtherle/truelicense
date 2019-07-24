/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.maven.plugin.generation

import org.scalatest.Matchers._
import org.scalatest.WordSpec
import org.scalatest.prop.TableDrivenPropertyChecks._

class ScalaToolSpec extends WordSpec {

  "A ScalaTool " should {
    "generate an obfuscated string " which {
      "matches the pattern" in {
        forAll(Table(
          "string",
          "",
          "Hello world!"
        )) { string =>
          new ScalaTool obfuscatedString string should (fullyMatch regex "new global\\.namespace\\.truelicense\\.obfuscate\\.ObfuscatedString\\(Array\\[Long\\]\\([^)]*\\)\\) /\\*.*\\*/")
        }
      }
    }
  }
}
