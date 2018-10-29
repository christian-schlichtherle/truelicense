/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.it.v2.json

import global.namespace.truelicense.api.LicenseManagementContextBuilder
import global.namespace.truelicense.it.v2.core.V2TestContext
import global.namespace.truelicense.v2.json.V2Json

/** @author Christian Schlichtherle */
trait V2JsonTestContext extends V2TestContext {

  final override def managementContextBuilder: LicenseManagementContextBuilder = V2Json.builder
}
