/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.truelicense.api

import java.util.Locale

import net.truelicense.api.i18n.Message
import org.junit.runner.RunWith
import org.scalatest.Matchers._
import org.scalatest.WordSpec
import org.scalatest.junit.JUnitRunner

/**
 * @author Christian Schlichtherle
 */
@RunWith(classOf[JUnitRunner])
class LicenseValidationExceptionTest extends WordSpec {

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
