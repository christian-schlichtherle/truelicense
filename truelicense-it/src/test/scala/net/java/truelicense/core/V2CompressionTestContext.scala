/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package net.java.truelicense.core

import net.java.truelicense.it.TestContext

/** @author Christian Schlichtherle */
trait V2CompressionTestContext { this: TestContext =>
  final override def transformation = new V2Compression
}
