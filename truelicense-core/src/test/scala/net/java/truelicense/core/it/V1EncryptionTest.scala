/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package net.java.truelicense.core.it

import net.java.truelicense.core.V1EncryptionTestContext
import org.junit.runner._
import org.scalatest.junit._

/** @author Christian Schlichtherle */
@RunWith(classOf[JUnitRunner])
class V1EncryptionTest
extends CodecTestSuite
   with V1TestContext
   with V1EncryptionTestContext
