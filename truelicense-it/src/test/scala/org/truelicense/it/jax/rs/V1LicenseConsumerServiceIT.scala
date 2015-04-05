/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.it.jax.rs

import org.truelicense.it.v1.V1TestContext

/** @author Christian Schlichtherle */
class V1LicenseConsumerServiceIT
extends LicenseConsumerServiceITSuite
with V1TestContext {
  override def extraData = null
}
