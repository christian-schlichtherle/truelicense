/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.it.jax.rs

import org.truelicense.it.v2.json.V2JsonTestContext

/** @author Christian Schlichtherle */
class V2JsonConsumerLicenseManagementServiceIT
extends ConsumerLicenseManagementServiceITSuite
with V2JsonTestContext {
  override def extraData = null
}
