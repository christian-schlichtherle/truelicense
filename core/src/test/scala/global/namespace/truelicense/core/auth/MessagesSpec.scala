/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package global.namespace.truelicense.core.auth

import global.namespace.truelicense.core.MessagesTestSupport._
import org.scalatest.WordSpec
import org.scalatest.prop.PropertyChecks._

/**
 * @author Christian Schlichtherle
 */
class MessagesSpec extends WordSpec {

  "Messages" should {
    "be binary serializable" in {
      import Notary._
      val table = Table(
        ("key"),
        (NO_PRIVATE_KEY),
        (NO_CERTIFICATE),
        (NO_SUCH_ENTRY),
        ("agpl3")
      )
      forAll (table) { key => testSerialization(Messages message key) }
    }
  }
}
