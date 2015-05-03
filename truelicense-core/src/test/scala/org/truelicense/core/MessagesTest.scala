/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.core

import org.truelicense.core.MessagesTestSupport._
import org.junit.runner.RunWith
import org.scalatest.WordSpec
import org.scalatest.junit.JUnitRunner
import org.scalatest.prop.PropertyChecks._

/**
 * @author Christian Schlichtherle
 */
@RunWith(classOf[JUnitRunner])
class MessagesTest extends WordSpec {

  "Messages" should {
    "be binary serializable" in {
      import TrueLicenseInitialization._
      import TrueLicenseValidation._
      val table = Table(
        "key",
        UNKNOWN,
        INVALID_SUBJECT,
        HOLDER_IS_NULL,
        ISSUER_IS_NULL,
        ISSUED_IS_NULL,
        LICENSE_IS_NOT_YET_VALID,
        LICENSE_HAS_EXPIRED,
        CONSUMER_TYPE_IS_NULL,
        CONSUMER_AMOUNT_IS_NOT_POSITIVE
      )
      forAll (table) { key => testSerialization(Messages message key) }
    }
  }
}
