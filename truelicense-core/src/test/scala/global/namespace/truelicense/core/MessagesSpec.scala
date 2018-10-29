/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package global.namespace.truelicense.core

import Messages._
import MessagesTestSupport._
import org.scalatest.WordSpec
import org.scalatest.prop.PropertyChecks._

/**
 * @author Christian Schlichtherle
 */
class MessagesSpec extends WordSpec {

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
