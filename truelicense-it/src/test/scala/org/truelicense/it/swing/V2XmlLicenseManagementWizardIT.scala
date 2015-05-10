/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.it.swing

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.truelicense.it.v2.xml.V2XmlTestContext

/** @author Christian Schlichtherle */
@RunWith(classOf[JUnitRunner])
class V2XmlLicenseManagementWizardIT
  extends LicenseManagementWizardITSuite
  with V2XmlTestContext
