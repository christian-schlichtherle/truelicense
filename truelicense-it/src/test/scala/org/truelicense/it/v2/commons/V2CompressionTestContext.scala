/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.it.v2.commons

import org.truelicense.it.core.TestContext
import org.truelicense.v2.commons.comp.V2Compression

/** @author Christian Schlichtherle */
trait V2CompressionTestContext { this: TestContext[_] =>
  final override def transformation = new V2Compression
}
