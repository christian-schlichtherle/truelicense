/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.it.v2.xml

import org.truelicense.it.core.LicenseKeyLifeCycleTestSuite
import org.junit.runner._
import org.scalatest.junit._

/** @author Christian Schlichtherle */
@RunWith(classOf[JUnitRunner])
class V2XmlLicenseKeyLifeCycleTest
  extends LicenseKeyLifeCycleTestSuite
  with V2XmlTestContext
