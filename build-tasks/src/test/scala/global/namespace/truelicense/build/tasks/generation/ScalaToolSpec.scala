/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.build.tasks.generation

import org.scalatest.matchers.should.Matchers._
import org.scalatest.prop.TableDrivenPropertyChecks._
import org.scalatest.wordspec.AnyWordSpec

class ScalaToolSpec extends AnyWordSpec {

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
