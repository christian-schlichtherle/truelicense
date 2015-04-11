/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.api

import java.util.Locale

import org.junit.runner.RunWith
import org.scalatest.Matchers._
import org.scalatest.WordSpec
import org.scalatest.junit.JUnitRunner
import org.truelicense.api.i18n.Message

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
        ex.getLocalizedMessage should be (message)
      }

      "return the localized message upon a call to getMessage()" in {
        ex.getMessage should be (message)
      }
    }
  }
}
