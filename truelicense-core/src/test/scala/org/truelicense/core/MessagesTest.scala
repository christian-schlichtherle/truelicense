/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.core

import org.truelicense.core.MessagesTestSupport._
import org.junit.runner.RunWith
import org.scalatest.WordSpec
import org.scalatest.junit.JUnitRunner
import org.scalatest.prop.PropertyChecks._
import Messages._

/**
 * @author Christian Schlichtherle
 */
@RunWith(classOf[JUnitRunner])
class MessagesTest extends WordSpec {

  "Messages" should {
    "be binary serializable" in {
      val table = Table(
        "key",
        CONSUMER_AMOUNT_IS_NOT_POSITIVE,
        CONSUMER_TYPE_IS_NULL,
        HOLDER_IS_NULL,
        INVALID_SUBJECT,
        ISSUED_IS_NULL,
        ISSUER_IS_NULL,
        LICENSE_HAS_EXPIRED,
        LICENSE_IS_NOT_YET_VALID,
        UNKNOWN
      )
      forAll (table) { key => testSerialization(Messages message key) }
    }
  }
}
