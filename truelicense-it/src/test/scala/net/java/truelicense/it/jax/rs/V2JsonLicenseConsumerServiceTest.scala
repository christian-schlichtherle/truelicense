/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.java.truelicense.it.jax.rs

import net.java.truelicense.it.v2.json.V2JsonTestContext
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

/** @author Christian Schlichtherle */
@RunWith(classOf[JUnitRunner])
class V2JsonLicenseConsumerServiceTest
extends LicenseConsumerServiceTestSuite
with V2JsonTestContext
