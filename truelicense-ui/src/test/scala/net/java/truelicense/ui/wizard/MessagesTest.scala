/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package net.java.truelicense.ui.wizard

import net.java.truelicense.ui.wizard.WizardMessage._
import org.junit.runner.RunWith
import org.scalatest.WordSpec
import org.scalatest.junit.JUnitRunner
import org.scalatest.prop.PropertyChecks._
import net.java.truelicense.core.MessagesTestSupport._

/**
  * @author Christian Schlichtherle
  */
@RunWith(classOf[JUnitRunner])
class MessagesTest extends WordSpec {

  "Messages" should {
    "be binary serializable" in {
      val table = Table(
        ("key"),
        (wizard_back),
        (wizard_next),
        (wizard_cancel),
        (wizard_finish)
        )
      forAll (table) { key => testSerialization(key format ()) }
    }
  }
}
