/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.java.truelicense.it.core

import java.io.File

import net.java.truelicense.core.io.FileStore

/** @author Christian Schlichtherle */
trait FileStoreITContext { this: TestContext =>
  override final def store = {
    val file = File.createTempFile("truelicense", null)
    file delete ()
    new FileStore(file)
  }
}
