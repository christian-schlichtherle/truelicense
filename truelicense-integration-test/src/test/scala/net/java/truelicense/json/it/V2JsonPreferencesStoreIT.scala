/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package net.java.truelicense.json.it

import org.junit.runner._
import org.scalatest.junit._
import net.java.truelicense.core.it._

/** @author Christian Schlichtherle */
@RunWith(classOf[JUnitRunner])
class V2JsonPreferencesStoreIT
extends CodecTestSuite
   with V2JsonTestContext
   with PreferencesStoreITContext
