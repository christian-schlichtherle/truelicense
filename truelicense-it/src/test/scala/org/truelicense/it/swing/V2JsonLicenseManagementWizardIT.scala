/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.it.swing

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.truelicense.it.v2.json.V2JsonTestContext

/** @author Christian Schlichtherle */
@RunWith(classOf[JUnitRunner])
class V2JsonLicenseManagementWizardIT
  extends LicenseManagementWizardITSuite
  with V2JsonTestContext
