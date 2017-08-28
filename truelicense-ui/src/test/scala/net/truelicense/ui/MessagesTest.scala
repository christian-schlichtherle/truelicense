/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.truelicense.ui

import net.truelicense.ui.LicenseWizardMessage._
import net.truelicense.ui.MessagesTestSupport._
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
        wizard_title,
        welcome_title,
        welcome_prompt,
        welcome_install,
        welcome_display,
        welcome_uninstall,
        install_title,
        install_prompt,
        install_select,
        install_install,
        install_success,
        install_failure,
        install_fileExtension,
        install_fileFilter,
        display_title,
        display_subject,
        display_holder,
        display_issuer,
        display_issued,
        display_notBefore,
        display_notAfter,
        display_consumer,
        display_consumerFormat,
        display_info,
        display_dateTimeFormat,
        display_failure,
        uninstall_title,
        uninstall_prompt,
        uninstall_uninstall,
        uninstall_success,
        uninstall_failure,
        failure_title
      )
      forAll (table) { key => testSerialization(key format ()) }
    }
  }
}
