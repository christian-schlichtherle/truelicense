/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.truelicense.it.v2.json

import net.truelicense.it.core.PathStoreITContext
import org.junit.runner._
import org.scalatest.junit._

/** @author Christian Schlichtherle */
@RunWith(classOf[JUnitRunner])
class V2JsonPathStoreIT
  extends V2JsonLicenseCodecTest
  with PathStoreITContext
