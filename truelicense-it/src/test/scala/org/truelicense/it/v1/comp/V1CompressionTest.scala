/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.it.v1.comp

import org.junit.runner._
import org.scalatest.junit._
import org.truelicense.it.core.CodecTestSuite
import org.truelicense.it.v1.V1TestContext

/** @author Christian Schlichtherle */
@RunWith(classOf[JUnitRunner])
class V1CompressionTest
extends CodecTestSuite
   with V1TestContext
   with V1CompressionTestContext
