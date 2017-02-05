/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.it.v2.json

import org.truelicense.it.v2.commons.V2TestContext
import org.truelicense.v2.json.V2JsonLicenseApplicationContext

/** @author Christian Schlichtherle */
trait V2JsonTestContext extends V2TestContext {

  override final val applicationContext = new V2JsonLicenseApplicationContext
}
