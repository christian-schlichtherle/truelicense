/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package net.java.truelicense.it.v2.xml

import net.java.truelicense.core.V2EncryptionTestContext
import net.java.truelicense.it.core.CodecTestSuite
import org.junit.runner._
import org.scalatest.junit._

/** @author Christian Schlichtherle */
@RunWith(classOf[JUnitRunner])
class V2XmlEncryptionTest
extends CodecTestSuite
   with V2XmlTestContext
   with V2EncryptionTestContext
