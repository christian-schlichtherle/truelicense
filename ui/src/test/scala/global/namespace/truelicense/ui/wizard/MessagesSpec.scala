/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.ui.wizard

import global.namespace.truelicense.ui.MessagesTestSupport._
import global.namespace.truelicense.ui.wizard.WizardMessage._
import org.scalatest.prop.TableDrivenPropertyChecks._
import org.scalatest.wordspec.AnyWordSpec

class MessagesSpec extends AnyWordSpec {

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
