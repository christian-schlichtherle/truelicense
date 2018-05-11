/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package net.truelicense.it.v2.json

import net.truelicense.api.LicenseManagementContextBuilder
import net.truelicense.it.v2.commons.V2TestContext
import net.truelicense.v2.json.V2Json

/** @author Christian Schlichtherle */
trait V2JsonTestContext extends V2TestContext {

  final override def managementContextBuilder: LicenseManagementContextBuilder = V2Json.builder
}
