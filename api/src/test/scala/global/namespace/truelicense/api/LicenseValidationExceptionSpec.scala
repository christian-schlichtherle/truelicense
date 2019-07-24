/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package global.namespace.truelicense.api

import java.util.Locale

import global.namespace.truelicense.api.i18n.Message
import org.scalatest.Matchers._
import org.scalatest.WordSpec

/**
 * @author Christian Schlichtherle
 */
class LicenseValidationExceptionSpec extends WordSpec {

  "A LicenseValidationException" when {
    "created with a localized message" should {
      val message = "test"
      val ex = new LicenseValidationException(new Message {
        override def toString() = toString(Locale.getDefault)
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
