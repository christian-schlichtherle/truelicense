/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.java.truelicense.it.jax.rs

import net.java.truelicense.it.v2.xml.V2XmlTestContext

/** @author Christian Schlichtherle */
class V2XmlLicenseConsumerServiceIT
extends LicenseConsumerServiceITSuite
with V2XmlTestContext {
  override def extraData: AnyRef = null
}
