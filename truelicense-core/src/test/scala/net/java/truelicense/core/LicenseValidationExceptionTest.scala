/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.java.truelicense.core

import net.java.truelicense.core.codec.Codecs
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
      val ex = new LicenseValidationException(message)

      "return the localized message upon a call to getLocalizedMessage()" in {
        ex.getLocalizedMessage should be (message)
      }

      "return the localized message upon a call to getMessage()" in {
        ex.getMessage should be (message)
      }

      "be serializable" in {
        val ex2 = Codecs clone ex
        ex2.getLocalizedMessage should be (message)
        ex2.getMessage should be (message)
      }
    }
  }
}
