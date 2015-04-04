/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.java.truelicense.it.v1

import net.java.truelicense.it.core.{CodecTestSuite, FileStoreITContext}
import org.junit.runner._
import org.scalatest.junit._

/** @author Christian Schlichtherle */
@RunWith(classOf[JUnitRunner])
class V1FileStoreIT
extends CodecTestSuite
   with V1TestContext
   with FileStoreITContext
