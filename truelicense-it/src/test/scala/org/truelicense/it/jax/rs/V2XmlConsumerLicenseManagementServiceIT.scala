/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.it.jax.rs

import org.truelicense.it.v2.xml.V2XmlTestContext

/** @author Christian Schlichtherle */
class V2XmlConsumerLicenseManagementServiceIT
extends ConsumerLicenseManagementServiceITSuite
with V2XmlTestContext {
  override def extraData = null
}
