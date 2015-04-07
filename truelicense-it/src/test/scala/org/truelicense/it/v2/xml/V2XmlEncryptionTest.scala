/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.it.v2.xml

import org.truelicense.it.core.CodecTestSuite
import org.junit.runner._
import org.scalatest.junit._
import org.truelicense.it.v2.base.V2EncryptionTestContext

/** @author Christian Schlichtherle */
@RunWith(classOf[JUnitRunner])
class V2XmlEncryptionTest
extends CodecTestSuite
   with V2XmlTestContext
   with V2EncryptionTestContext
