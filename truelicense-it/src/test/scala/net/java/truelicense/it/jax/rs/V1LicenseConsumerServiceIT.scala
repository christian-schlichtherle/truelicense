/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.java.truelicense.it.jax.rs

import net.java.truelicense.it.v1.V1TestContext

/** @author Christian Schlichtherle */
class V1LicenseConsumerServiceIT
extends LicenseConsumerServiceITSuite
with V1TestContext {
  override def extraData: AnyRef = null
}
