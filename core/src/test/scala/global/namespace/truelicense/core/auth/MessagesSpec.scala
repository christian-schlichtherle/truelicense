/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.core.auth

import global.namespace.truelicense.core.MessagesTestSupport._
import org.scalatest.WordSpec
import org.scalatest.prop.TableDrivenPropertyChecks._

class MessagesSpec extends WordSpec {

  "Messages" should {
    "be binary serializable" in {
      import Notary._
      val table = Table(
        ("key"),
        (NO_PRIVATE_KEY),
        (NO_CERTIFICATE),
        (NO_SUCH_ENTRY),
      )
      forAll(table) { key => testSerialization(Messages message key) }
    }
  }
}
