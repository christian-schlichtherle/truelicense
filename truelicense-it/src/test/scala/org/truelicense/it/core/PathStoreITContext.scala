/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.it.core

import java.nio.file.Files

import org.truelicense.core.io.PathStore

/** @author Christian Schlichtherle */
trait PathStoreITContext { this: TestContext =>
  override final def store = {
    val path = Files.createTempFile("truelicense", ".tmp")
    Files delete path
    new PathStore(path)
  }
}
