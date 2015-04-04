/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package net.java.truelicense.it.ws.rs

import net.java.truelicense.it.json.V2JsonTestContext

/** @author Christian Schlichtherle */
class V2JsonLicenseConsumerServiceIT
extends LicenseConsumerServiceITSuite
with V2JsonTestContext {
  override def extraData = null
}
