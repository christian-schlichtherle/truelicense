/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.api

import global.namespace.truelicense.api.i18n.Message
import org.scalatest.matchers.should.Matchers._
import org.scalatest.wordspec.AnyWordSpec

import java.util.Locale

class LicenseValidationExceptionSpec extends AnyWordSpec {

  "A LicenseValidationException" when {
    "created with a localized message" should {
      val message = "test"
      val ex = new LicenseValidationException(new Message {

        override def toString: String = toString(Locale.getDefault)

        def toString(locale: Locale) = "test"
      })

      "return the localized message upon a call to getLocalizedMessage()" in {
        ex.getLocalizedMessage shouldBe message
      }

      "return the localized message upon a call to getMessage()" in {
        ex.getMessage shouldBe message
      }
    }
  }
}
