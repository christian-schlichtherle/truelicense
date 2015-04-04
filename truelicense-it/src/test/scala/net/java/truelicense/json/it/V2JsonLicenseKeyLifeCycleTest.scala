/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package net.java.truelicense.json.it

import net.java.truelicense.core.it.LicenseKeyLifeCycleTestSuite
import org.junit.runner._
import org.scalatest.junit._

/** @author Christian Schlichtherle */
@RunWith(classOf[JUnitRunner])
class V2JsonLicenseKeyLifeCycleTest
extends LicenseKeyLifeCycleTestSuite
with V2JsonTestContext
