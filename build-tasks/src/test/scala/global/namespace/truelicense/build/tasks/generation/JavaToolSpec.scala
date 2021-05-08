/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.build.tasks.generation

import org.scalatest.matchers.should.Matchers._
import org.scalatest.prop.TableDrivenPropertyChecks._
import org.scalatest.wordspec.AnyWordSpec

class JavaToolSpec extends AnyWordSpec {

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
