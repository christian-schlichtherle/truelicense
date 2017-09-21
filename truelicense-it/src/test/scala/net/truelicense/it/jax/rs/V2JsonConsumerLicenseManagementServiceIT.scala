/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.truelicense.it.jax.rs

import net.truelicense.it.v2.json.V2JsonTestContext

/** @author Christian Schlichtherle */
class V2JsonConsumerLicenseManagementServiceIT
extends ConsumerLicenseManagementServiceITSuite
with V2JsonTestContext {
  override def extraData: AnyRef = null
}
