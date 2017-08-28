/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.truelicense.it.jax.rs

import net.truelicense.it.v2.xml.V2XmlTestContext

/** @author Christian Schlichtherle */
class V2XmlConsumerLicenseManagementServiceIT
extends ConsumerLicenseManagementServiceITSuite
with V2XmlTestContext {
  override def extraData = null
}
