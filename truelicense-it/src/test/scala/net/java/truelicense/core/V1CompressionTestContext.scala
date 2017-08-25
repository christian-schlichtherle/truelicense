/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.java.truelicense.core

import net.java.truelicense.core.io.Transformation
import net.java.truelicense.it.core.TestContext

/** @author Christian Schlichtherle */
trait V1CompressionTestContext { this: TestContext =>
  final override def transformation: Transformation = new V1Compression
}
