/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.ui.wizard

import org.truelicense.ui.MessagesTestSupport._
import org.truelicense.ui.wizard.WizardMessage._
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
