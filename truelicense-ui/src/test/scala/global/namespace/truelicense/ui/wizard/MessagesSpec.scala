/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package global.namespace.truelicense.ui.wizard

import global.namespace.truelicense.ui.MessagesTestSupport._
import WizardMessage._
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
        wizard_back,
        wizard_next,
        wizard_cancel,
        wizard_finish
      )
      forAll (table) { key => testSerialization(key format ()) }
    }
  }
}
