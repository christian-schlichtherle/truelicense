/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.truelicense.it.core

import java.nio.file.Files

/** @author Christian Schlichtherle */
trait PathStoreITContext { this: TestContext[_] =>
  final override def store = {
    val path = Files.createTempFile("truelicense", ".tmp")
    Files delete path
    managementContext.pathStore(path)
  }
}
