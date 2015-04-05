/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.core

import org.truelicense.it.core.TestContext

/** @author Christian Schlichtherle */
trait V1CompressionTestContext { this: TestContext =>
  final override def transformation = new V1Compression
}
