/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.truelicense.core.auth

import net.truelicense.core.MessagesTestSupport._
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
